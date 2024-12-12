package vn.edu.iuh.fit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.iuh.fit.models.CandidateSkill;
import vn.edu.iuh.fit.models.CandidateSkillId;
import vn.edu.iuh.fit.models.Skill;

import java.util.List;

public interface CandidateSkillRepository extends JpaRepository<CandidateSkill, CandidateSkillId> {
    //tim theo candidate id
    @Query("SELECT cs FROM CandidateSkill cs WHERE cs.candidate.id = :canId")
    List<CandidateSkill> findByCanId(@Param("canId") Long canId);
    List<CandidateSkill> findByCandidateId(Long candidateId);
    @Query("SELECT cs.skill FROM CandidateSkill cs WHERE cs.candidate.id = :candidateId")
    List<Skill> findSkillsByCandidateId(Long candidateId);
}