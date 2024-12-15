package vn.edu.iuh.fit.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.iuh.fit.backend.models.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    // Tìm companyId theo username của nhà tuyển dụng
    @Query("SELECT c.id FROM Company c JOIN c.account a WHERE a.username = :recruiterUsername")
    Long findCompanyIdByRecruiterUsername(@Param("recruiterUsername") String recruiterUsername);
}