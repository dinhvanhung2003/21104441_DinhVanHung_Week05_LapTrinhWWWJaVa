package vn.edu.iuh.fit.frontend.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.iuh.fit.backend.models.Address;
import vn.edu.iuh.fit.backend.models.Company;
@Getter
@Setter
@NoArgsConstructor
public class CompanyFullInfo {
    private Company company;
    private Address address;

}

