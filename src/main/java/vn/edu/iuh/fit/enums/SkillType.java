package vn.edu.iuh.fit.enums;


public enum SkillType {
    SOFT_SKILL("Soft Skill"),
    UNSPECIFIC("Unspecified"),
    TECHNICAL_SKILL("Technical Skill");

    private final String description;

    SkillType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    // Optionally, you can implement a method to get SkillType by name or description
    public static SkillType fromDescription(String description) {
        for (SkillType type : SkillType.values()) {
            if (type.description.equalsIgnoreCase(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No SkillType found for description: " + description);
    }
}
