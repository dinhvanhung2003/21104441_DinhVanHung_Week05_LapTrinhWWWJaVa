package vn.edu.iuh.fit.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.edu.iuh.fit.enums.SkillLevelType;

@Getter
@Setter
@Entity
@Table(name = "job_skill")
public class JobSkill {
    @EmbeddedId
    private JobSkillId id;

    @MapsId("jobId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private vn.edu.iuh.fit.models.Job job;

    @Column(name = "more_infos", length = 1000)
    private String moreInfos;    @Column(name = "skill_level", nullable = false)
    @Enumerated(EnumType.STRING) // Stores enum as String in the database
    private SkillLevelType skillLevel;
    @MapsId("skillId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

}