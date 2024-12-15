package vn.edu.iuh.fit.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.edu.iuh.fit.backend.converts.SkillLevelConverter;
import vn.edu.iuh.fit.backend.enums.SkillLevelType;

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
    private Job job;

    @Column(name = "more_infos", length = 1000)
    private String moreInfos;
    @Column(name = "skill_level_id", nullable = false)
    @Convert(converter = SkillLevelConverter.class) // Use custom converter
    private SkillLevelType skillLevel;
    @MapsId("skillId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

}