package vn.edu.iuh.fit.backend.enums;

public enum SkillType {
    SOFT_SKILL(1, "Soft Skill"),
    UNSPECIFIC(2, "Unspecified"),
    TECHNICAL_SKILL(3, "Technical Skill");

    private final int id;
    private final String description;

    SkillType(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    // Optionally, you can implement a method to get SkillType by id or description
    public static SkillType fromId(int id) {
        for (SkillType type : SkillType.values()) {
            if (type.id == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("No SkillType found for id: " + id);
    }

    public static SkillType fromDescription(String description) {
        for (SkillType type : SkillType.values()) {
            if (type.description.equalsIgnoreCase(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No SkillType found for description: " + description);
    }
}
