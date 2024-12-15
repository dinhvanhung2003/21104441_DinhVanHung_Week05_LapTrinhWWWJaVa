package vn.edu.iuh.fit.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.fit.backend.models.JobSkill;
import vn.edu.iuh.fit.backend.models.JobSkillId;

import java.util.List;

public interface JobSkillRepository extends JpaRepository<JobSkill, JobSkillId> {
    // Tìm kỹ năng cần thiết cho công việc theo ID công việc
    List<JobSkill> findByIdJobId(Long jobId);
}