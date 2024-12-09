package vn.edu.iuh.fit.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.models.SkillLevel;


import vn.edu.iuh.fit.repositories.SkillLevelRepository;

import java.util.List;

@Service
public class SkillLevelService {
    @Autowired
    private SkillLevelRepository skillLevelRepository;

    public List<SkillLevel> findAll() {
        return skillLevelRepository.findAll();
    }

    public SkillLevel findByName(SkillLevel name) {
        return skillLevelRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Skill level not found: " + name));
    }

    public SkillLevel save(SkillLevel skillLevelEntity) {
        return skillLevelRepository.save(skillLevelEntity);
    }
}
