package vn.edu.iuh.fit.services;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.models.*;
import vn.edu.iuh.fit.repositories.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private JobSkillRepository jobSkillRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private CandidateSkillRepository candidateSkillRepository;
    // Lấy tất cả các job
    public List<Job> findAllJobs() {
        return jobRepository.findAll();
    }

    // Phân trang và sắp xếp danh sách job
    public Page<Job> findAll(int page, int size, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        return jobRepository.findAll(PageRequest.of(page, size, sort));
    }

    // Lưu job mới hoặc cập nhật job
    public Job saveJob(Job job) {
        return jobRepository.save(job);
    }

    // Tìm job theo ID
    public Optional<Job> findJobById(Long id) {
        return jobRepository.findById(id);
    }



    // Lấy danh sách công việc theo username của nhà tuyển dụng
    public List<Job> getJobsByRecruiterUsername(String recruiterUsername) {
        Long companyId = companyRepository.findCompanyIdByRecruiterUsername(recruiterUsername); // Lấy companyId
        return jobRepository.findByCompanyId(companyId); // Lấy công việc theo companyId
    }

    // Lưu công việc mới cho nhà tuyển dụng
    public Job saveJobForRecruiter(Job job, String recruiterUsername) {
        Long companyId = companyRepository.findCompanyIdByRecruiterUsername(recruiterUsername); // Lấy companyId
        job.setCompany(new Company(companyId)); // Gán công ty vào Job
        return jobRepository.save(job); // Lưu công việc
    }

    public Optional<Job> findJobByIdAndRecruiter(Long jobId, String recruiterUsername) {
        return jobRepository.findByIdAndRecruiterUsername(jobId, recruiterUsername);
    }
    // Lấy danh sách ứng viên theo jobId
    public List<Candidate> getCandidatesByJobId(Long jobId) {
        return jobRepository.findCandidatesByJobId(jobId);
    }


    // Đánh giá ứng viên và gửi email mời phỏng vấn

    @Transactional
    public List<Candidate> evaluateCandidatesAndSendInvites(Long jobId) throws MessagingException {
        // Lấy thông tin công việc
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Công việc không tồn tại"));

        System.out.println("Evaluating candidates for Job ID: " + job.getId() + ", Job Name: " + job.getJobName());

        // Lấy danh sách ứng viên liên quan đến công việc
        List<Candidate> candidates = getCandidatesByJobId(jobId);
        System.out.println("Total Candidates Found: " + candidates.size());

        // Đảm bảo danh sách kỹ năng của công việc không bị null hoặc rỗng
        if (job.getJobSkills() == null || job.getJobSkills().isEmpty()) {
            System.out.println("No skills are required for this job.");
            return List.of(); // Trả về danh sách rỗng
        }

        // Lọc ứng viên phù hợp
        List<Candidate> qualifiedCandidates = candidates.stream()
                .filter(candidate -> {
                    System.out.println("Evaluating Candidate ID: " + candidate.getId());

                    // Kiểm tra nếu ứng viên không có kỹ năng
                    if (candidate.getCandidateSkills() == null || candidate.getCandidateSkills().isEmpty()) {
                        System.out.println("Candidate ID: " + candidate.getId() + " has no skills.");
                        return false;
                    }

                    // Kiểm tra nếu ứng viên đáp ứng tất cả các kỹ năng và cấp độ yêu cầu của công việc
                    boolean isQualified = job.getJobSkills().stream().allMatch(jobSkill -> {
                        // Kiểm tra nếu ứng viên có kỹ năng phù hợp và đạt hoặc vượt cấp độ yêu cầu
                        boolean matched = candidate.getCandidateSkills().stream()
                                .anyMatch(candidateSkill -> {
                                    boolean isSkillMatch = candidateSkill.getSkill().getId().equals(jobSkill.getSkill().getId());
                                    boolean isLevelMatch = candidateSkill.getSkillLevel().getId() >= jobSkill.getSkillLevel().getId();

                                    System.out.println("Matching Skill: " + isSkillMatch +
                                            " | Matching Level: " + isLevelMatch +
                                            " | Candidate Skill: " + candidateSkill.getSkill().getSkillName() +
                                            " (Level: " + candidateSkill.getSkillLevel().getName() + ")" +
                                            " | Job Skill: " + jobSkill.getSkill().getSkillName() +
                                            " (Required Level: " + jobSkill.getSkillLevel().getName() + ")");

                                    return isSkillMatch && isLevelMatch;
                                });

                        System.out.println("Job Skill Match: " + matched);
                        return matched;
                    });

                    return isQualified;
                })
                .toList();

        System.out.println("Qualified Candidates Count: " + qualifiedCandidates.size());

        // Gửi email cho ứng viên đủ điều kiện
        for (Candidate candidate : qualifiedCandidates) {
            try {
                String subject = "Lời mời phỏng vấn cho vị trí " + job.getJobName();
                String body = """
                <p>Xin chào <strong>%s</strong>,</p>
                <p>Chúng tôi rất vui được mời bạn tham gia phỏng vấn cho vị trí <strong>%s</strong>.</p>
                <p>Vui lòng trả lời email này để xác nhận thời gian phỏng vấn.</p>
                <p>Trân trọng,<br>Công ty XYZ</p>
                """.formatted(candidate.getFullName(), job.getJobName());

                emailService.sendEmail(candidate.getEmail(), subject, body);
                System.out.println("Email sent to Candidate: " + candidate.getEmail());
            } catch (MessagingException e) {
                System.err.println("Failed to send email to Candidate: " + candidate.getEmail());
            }
        }

        return qualifiedCandidates;
    }



}
