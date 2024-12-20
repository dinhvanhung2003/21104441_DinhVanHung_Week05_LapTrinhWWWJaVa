package vn.edu.iuh.fit.backend.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "candidate")
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "dob", nullable = false)
    private LocalDate dob;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phone", nullable = false, length = 15)
    private String phone;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "address", nullable = false)
    private Address address;
    @ManyToMany
    @JoinTable(
            name = "candidate_job",
            joinColumns = @JoinColumn(name = "candidate_id"),
            inverseJoinColumns = @JoinColumn(name = "job_id")
    )
    private List<Job> jobs;
    public Candidate(String s, LocalDate of, Address add, String s1, String s2) {
    }

    @OneToOne(mappedBy = "candidate")
    private Account account;
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Experience> experiences = new ArrayList<>();
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CandidateSkill> candidateSkills = new ArrayList<>();
    // Getters và Setters
    public List<Experience> getExperiences() {
        return experiences;
    }

    public void setExperiences(List<Experience> experiences) {
        this.experiences = experiences;
    }



    // Getters và Setters
    public List<CandidateSkill> getCandidateSkills() {
        return candidateSkills;
    }

    public void setCandidateSkills(List<CandidateSkill> candidateSkills) {
        this.candidateSkills = candidateSkills;
    }
}