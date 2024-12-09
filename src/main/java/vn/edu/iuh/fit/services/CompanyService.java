package vn.edu.iuh.fit.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.models.Company;
import vn.edu.iuh.fit.repositories.CompanyRepository;

import java.util.Optional;

@Service
public class CompanyService {
    @Autowired
    private CompanyRepository companyRepository;

    // Phương thức để tìm công ty theo ID
    public Company findCompanyById(Long companyId) {
        Optional<Company> company = companyRepository.findById(companyId);
        return company.orElse(null);
    }

    public Company saveCompany(Company company) {
        return companyRepository.save(company);
    }

    public void deleteCompanyById(Long companyId) {
        companyRepository.deleteById(companyId);
    }

    public Iterable<Company> findAllCompanies() {
        return companyRepository.findAll();
    }
}
