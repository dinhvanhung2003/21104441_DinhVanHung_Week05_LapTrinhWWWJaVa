package vn.edu.iuh.fit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.fit.models.Experience;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {
}