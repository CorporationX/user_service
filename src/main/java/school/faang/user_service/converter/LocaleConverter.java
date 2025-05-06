package school.faang.user_service.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Locale;

@Converter(autoApply = true)
public class LocaleConverter implements AttributeConverter<Locale, String> {

    @Override
    public String convertToDatabaseColumn(Locale locale) {
        if (locale == null) return null;
        if (!locale.getCountry().isEmpty()) {
            return locale.getLanguage() + "-" + locale.getCountry();
        }
        return locale.getLanguage();
    }

    @Override
    public Locale convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return null;
        return Locale.forLanguageTag(dbData);
    }
}

