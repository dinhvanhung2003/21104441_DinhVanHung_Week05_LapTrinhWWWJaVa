package vn.edu.iuh.fit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.iuh.fit.models.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
  @Query("SELECT r FROM Role r WHERE r.roleName = :roleName")
  Optional<Role> findByRoleName(@Param("roleName") String roleName);
}