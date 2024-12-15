package vn.edu.iuh.fit.frontend.models;

import lombok.Getter;
import lombok.Setter;
import vn.edu.iuh.fit.backend.models.Address;
import vn.edu.iuh.fit.backend.models.CandidateSkill;
import vn.edu.iuh.fit.backend.models.Experience;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class Candidate {
    private vn.edu.iuh.fit.backend.models.Candidate candidate;
    private Address address;
    private List<Experience> experiences = new ArrayList<>();
    private List<CandidateSkill> candidateSkills = new ArrayList<>();
}
