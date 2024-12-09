package vn.edu.iuh.fit.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.models.*;
import vn.edu.iuh.fit.repositories.*;

import java.util.Collections;
import java.util.List;
@Service
public class CandidateService {
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private CandidateSkillRepository candidateSkillRepository;
    @Autowired
    private JobService jobService;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private ExperienceRepository experienceRepository;


    public Page<Candidate> findAll(int page, int size, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        return candidateRepository.findAll(PageRequest.of(page, size, sort));
    }

    public List<Candidate> findAll() {
        return candidateRepository.findAll();
    }

    // Lấy danh sách kỹ năng của ứng viên
    public List<CandidateSkill> getCandidateSkills(Long candidateId) {
        return candidateSkillRepository.findByCanId(candidateId);
    }
    @Transactional
    public void applyForJob(String candidateUsername, Long jobId) {
        // Lấy thông tin ứng viên
        Candidate candidate = candidateRepository.findByUsername(candidateUsername)
                .orElseThrow(() -> new IllegalArgumentException("Ứng viên không tồn tại"));

        // Lấy thông tin công việc
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Công việc không tồn tại"));

        // Kiểm tra nếu ứng viên đã ứng tuyển vào công việc này
        if (job.getCandidates().contains(candidate)) {
            throw new IllegalStateException("Bạn đã ứng tuyển vào công việc này.");
        }

        // Thêm ứng viên vào danh sách ứng viên của công việc
        job.getCandidates().add(candidate);
        jobRepository.save(job); // Lưu thay đổi
    }
    //dang ky va cap nhat ho so
    public Candidate registerCandidate(Candidate candidate) {
        return candidateRepository.save(candidate);
    }

    public void updateAddress(Long candidateId, Address address) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ứng viên"));
        address.setCandidate(candidate);
        addressRepository.save(address);
    }

    public void addExperience(Long candidateId, Experience experience) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ứng viên"));
        experience.setCandidate(candidate);
        experienceRepository.save(experience);
    }

    public void addSkill(Long candidateId, CandidateSkill candidateSkill) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ứng viên"));
        candidateSkill.setCandidate(candidate);
        candidateSkillRepository.save(candidateSkill);
    }
    public Candidate getCandidateById(Long id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Candidate with ID " + id + " not found"));
    }

    public Candidate saveCandidate(Candidate candidate) {
        return candidateRepository.save(candidate);
    }
}