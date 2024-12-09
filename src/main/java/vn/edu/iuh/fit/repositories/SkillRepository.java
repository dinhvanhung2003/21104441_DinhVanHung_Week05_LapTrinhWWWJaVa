package vn.edu.iuh.fit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.fit.models.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long> {
}