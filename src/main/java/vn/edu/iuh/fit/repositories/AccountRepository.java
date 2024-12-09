package vn.edu.iuh.fit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.iuh.fit.models.Account;


import java.util.Optional;
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    // Tìm account theo username
    Optional<Account> findByUsername(String username);
    boolean existsByUsername(String username);
}
