package vn.edu.iuh.fit.backend.enums;



public enum SkillLevelType {
        BEGINNER(1, "Beginner"),
        INTERMEDIATE(2, "Intermediate"),
        ADVANCED(3, "Advanced"),
        PROFESSIONAL(4, "Professional"),
        EXPERT(5, "Expert");

        private final int id;
        private final String name; // Thêm thuộc tính name

        // Constructor để gán id và name
        SkillLevelType(int id, String name) {
            this.id = id;
            this.name = name;
        }

        // Getter cho id
        public int getId() {
            return id;
        }

        // Getter cho name
        public String getName() {
            return name;
        }

        // Tìm enum từ id
        public static SkillLevelType fromId(int id) {
            for (SkillLevelType level : values()) {
                if (level.getId() == id) {
                    return level;
                }
            }
            throw new IllegalArgumentException("Invalid SkillLevelType id: " + id);
        }

        // Tìm enum từ name
        public static SkillLevelType fromName(String name) {
            for (SkillLevelType level : values()) {
                if (level.getName().equalsIgnoreCase(name)) {
                    return level;
                }
            }
            throw new IllegalArgumentException("Invalid SkillLevelType name: " + name);
        }
}


