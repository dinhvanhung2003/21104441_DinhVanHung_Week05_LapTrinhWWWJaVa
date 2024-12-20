package vn.edu.iuh.fit.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.iuh.fit.backend.models.Candidate;
import vn.edu.iuh.fit.backend.models.Job;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {
    // Lấy danh sách công việc theo companyId
    List<Job> findByCompanyId(Long companyId);

    // Lấy công việc theo jobId và companyId
    Optional<Job> findByIdAndCompanyId(Long jobId, Long companyId);

    @Query("SELECT j FROM Job j WHERE j.id = :jobId AND j.company.account.username = :recruiterUsername")
    Optional<Job> findByIdAndRecruiterUsername(@Param("jobId") Long jobId, @Param("recruiterUsername") String recruiterUsername);

    // Lấy danh sách ứng viên theo jobId
    @Query("SELECT c FROM Candidate c JOIN c.jobs j WHERE j.id = :jobId")
    List<Candidate> findCandidatesByJobId(@Param("jobId") Long jobId);
    Optional<Job> findById(Long id);
    List<Job> findByCandidates(Candidate candidate);
    Page<Job> findByJobNameContainingIgnoreCase(String jobName, Pageable pageable);
    @Query("SELECT j FROM Job j WHERE LOWER(j.jobName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(j.company.compName) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Job> searchByJobNameOrCompany(@Param("query") String query, Pageable pageable);
    Page<Job> findAll(Pageable pageable);

    List<Job> findByJobNameContainingIgnoreCaseAndCompanyId(String name, Long companyId);


}