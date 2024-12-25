package school.faang.user_service.model.recommendation;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LanguageConverter implements AttributeConverter<Language, String> {

    @Override
    public String convertToDatabaseColumn(Language language) {
        if (language == null) {
            return null;
        }
        return language.getTag();
    }

    @Override
    public Language convertToEntityAttribute(String tag) {
        if (tag == null) {
            return null;
        }
        for (Language language : Language.values()) {
            if (language.getTag().equals(tag)) {
                return language;
            }
        }
        throw new IllegalArgumentException("Unknown tag: " + tag);
    }
}
