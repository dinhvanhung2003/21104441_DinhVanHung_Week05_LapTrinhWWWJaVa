package vn.edu.iuh.fit.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.edu.iuh.fit.backend.enums.SkillLevelType;

@Getter
@Setter
@Entity
@Table(name = "skill_level")
public class SkillLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Enumerated(EnumType.STRING) // Lưu Enum dưới dạng chuỗi
    @Column(name = "name", nullable = false)
    private SkillLevelType name;

    public SkillLevel() {
    }

    public SkillLevel(SkillLevelType name) {
        this.name = name;
    }
}
