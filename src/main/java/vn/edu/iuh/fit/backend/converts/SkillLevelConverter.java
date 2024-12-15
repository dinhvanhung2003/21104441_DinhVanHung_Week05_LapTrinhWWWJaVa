package vn.edu.iuh.fit.backend.converts;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.edu.iuh.fit.backend.enums.SkillLevelType;

@Converter(autoApply = true)
public class SkillLevelConverter implements AttributeConverter<SkillLevelType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(SkillLevelType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getId();
    }

    @Override
    public SkillLevelType convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return SkillLevelType.fromId(dbData);
    }
}
