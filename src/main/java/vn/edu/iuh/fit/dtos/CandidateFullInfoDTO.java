package vn.edu.iuh.fit.dtos;

import lombok.Getter;
import lombok.Setter;
import vn.edu.iuh.fit.models.Address;
import vn.edu.iuh.fit.models.Candidate;
import vn.edu.iuh.fit.models.CandidateSkill;
import vn.edu.iuh.fit.models.Experience;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class CandidateFullInfoDTO {
    private Candidate candidate;
    private Address address;
    private List<Experience> experiences = new ArrayList<>();
    private List<CandidateSkill> candidateSkills = new ArrayList<>();
}
