package vn.edu.iuh.fit.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.backend.models.Role;
import vn.edu.iuh.fit.backend.repositories.RoleRepository;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public Role getOrCreateRoleCandidate() {
        return roleRepository.findByRoleName("ROLE_CANDIDATE")
                .orElseGet(() -> {
                    Role roleCandidate = new Role();
                    roleCandidate.setRoleName("ROLE_CANDIDATE");
                    return roleRepository.save(roleCandidate);
                });
    }
}
