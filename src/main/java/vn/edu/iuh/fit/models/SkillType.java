package vn.edu.iuh.fit.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "skill_type")
public class SkillType {
    @Id
    @Column(name = "id", nullable = false)
    private Byte id;

    @Lob
    @Column(name = "name", nullable = false)
    private String name;

}