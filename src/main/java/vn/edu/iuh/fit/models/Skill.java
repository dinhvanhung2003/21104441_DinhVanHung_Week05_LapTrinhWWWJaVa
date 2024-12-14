package vn.edu.iuh.fit.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.edu.iuh.fit.enums.SkillType;

@Getter
@Setter
@Entity
@Table(name = "skill")
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_id", nullable = false)
    private Long id;

    @Column(name = "skill_description")
    private String skillDescription;

    @Column(name = "skill_name")
    private String skillName;
    @Column(name = "skill_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SkillType type = SkillType.SOFT_SKILL;
}