package vn.edu.iuh.fit.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.backend.models.Candidate;
import vn.edu.iuh.fit.backend.models.EmailLog;
import vn.edu.iuh.fit.backend.models.Job;
import vn.edu.iuh.fit.backend.repositories.EmailLogRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmailLogService {
    @Autowired
    private EmailLogRepository emailLogRepository;

    public void logEmail(Candidate candidate, Job job, String status, String message) {
        EmailLog emailLog = new EmailLog();
        emailLog.setCandidate(candidate);
        emailLog.setJob(job);
        emailLog.setStatus(status);
        emailLog.setMessage(message);
        emailLog.setSentAt(LocalDateTime.now());
        emailLogRepository.save(emailLog);
    }

    public List<EmailLog> getEmailLogsByJobId(Long jobId) {
        return emailLogRepository.findByJobId(jobId);
    }
}
