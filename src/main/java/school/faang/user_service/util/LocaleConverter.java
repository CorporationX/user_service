package school.faang.user_service.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Locale;

@Converter(autoApply = true)
public class LocaleConverter implements AttributeConverter<Locale, String> {
    @Override
    public String convertToDatabaseColumn(Locale locale) {
        return locale != null ? locale.toString() : null;
    }

    @Override
    public Locale convertToEntityAttribute(String dbData) {
        return dbData != null ? Locale.forLanguageTag(dbData.replace('-', '_')) : null;
    }
}
