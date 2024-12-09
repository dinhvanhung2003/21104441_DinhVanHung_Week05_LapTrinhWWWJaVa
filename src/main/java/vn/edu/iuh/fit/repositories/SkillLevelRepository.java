package vn.edu.iuh.fit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.fit.models.SkillLevel;

import java.util.Optional;

public interface SkillLevelRepository extends JpaRepository<SkillLevel, Byte> {
  Optional<SkillLevel> findByName(SkillLevel name);
}