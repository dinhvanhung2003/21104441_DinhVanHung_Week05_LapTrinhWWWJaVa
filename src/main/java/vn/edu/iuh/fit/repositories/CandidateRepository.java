package vn.edu.iuh.fit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.iuh.fit.models.Candidate;

import java.util.Optional;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
  @Query("SELECT c FROM Candidate c WHERE c.id = (SELECT a.candidate.id FROM Account a WHERE a.username = :username)")
  Optional<Candidate> findByUsername(@Param("username") String username);
  }