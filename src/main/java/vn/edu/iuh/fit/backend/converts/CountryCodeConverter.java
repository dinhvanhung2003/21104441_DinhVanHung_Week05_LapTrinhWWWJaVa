package vn.edu.iuh.fit.backend.converts;

import com.neovisionaries.i18n.CountryCode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CountryCodeConverter implements AttributeConverter<CountryCode, Short> {
    @Override
    public Short convertToDatabaseColumn(CountryCode attribute) {
        return attribute != null ? (short) attribute.getNumeric() : null;
    }

    @Override
    public CountryCode convertToEntityAttribute(Short dbData) {
        return dbData != null ? CountryCode.getByCode(dbData) : null;
    }
}
