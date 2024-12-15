package vn.edu.iuh.fit.backend.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.backend.models.Candidate;
import vn.edu.iuh.fit.backend.models.Job;
import vn.edu.iuh.fit.backend.repositories.CandidateRepository;
import vn.edu.iuh.fit.backend.repositories.JobRepository;

@Service
public class JobApplicationService {

    private final CandidateRepository candidateRepository;
    private final JobRepository jobRepository;

    public JobApplicationService(CandidateRepository candidateRepository, JobRepository jobRepository) {
        this.candidateRepository = candidateRepository;
        this.jobRepository = jobRepository;
    }

    @Transactional
    public void applyForJob(Long jobId, String username) {
        Candidate candidate = candidateRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // Liên kết Candidate và Job
        candidate.getJobs().add(job);
        job.getCandidates().add(candidate);

        // Lưu cập nhật
        candidateRepository.save(candidate);
    }
}

