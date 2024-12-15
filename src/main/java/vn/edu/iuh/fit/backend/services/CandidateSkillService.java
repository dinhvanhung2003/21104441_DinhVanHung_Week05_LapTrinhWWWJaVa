package vn.edu.iuh.fit.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.backend.models.Skill;
import vn.edu.iuh.fit.backend.repositories.CandidateSkillRepository;

import java.util.List;

@Service
public class CandidateSkillService {
    @Autowired
    private CandidateSkillRepository candidateSkillRepository;

    public List<Skill> findSkillsByCandidateId(Long candidateId) {
        return candidateSkillRepository.findSkillsByCandidateId(candidateId);
    }

}
