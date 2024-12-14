package vn.edu.iuh.fit.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.models.JobSkill;
import vn.edu.iuh.fit.repositories.JobSkillRepository;

import java.util.List;

@Service
public class JobSkillService {
    @Autowired
    private JobSkillRepository jobSkillRepository;

    public List<JobSkill> findSkillsByJobId(Long jobId) {
        return jobSkillRepository.findByIdJobId(jobId);
    }
    // Hàm lưu JobSkill vào cơ sở dữ liệu
    public JobSkill save(JobSkill jobSkill) {
        return jobSkillRepository.save(jobSkill);
    }
}
