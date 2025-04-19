package school.faang.user_service.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class LocaleConverterTest {
    private LocaleConverter converter;

    @BeforeEach
    void setUp() {
        converter = new LocaleConverter();
    }

    @Nested
    @DisplayName("convertToDatabaseColumn")
    class ConvertToDatabaseColumn {

        @Test
        @DisplayName("Should convert full Locale with country to correct format")
        void testConvertLocaleWithCountry() {
            Locale locale = new Locale("en", "US");

            String result = converter.convertToDatabaseColumn(locale);

            assertEquals("en-US", result);
        }

        @Test
        @DisplayName("Should convert Locale without country")
        void testConvertLocaleWithoutCountry() {
            Locale locale = new Locale("fr", "");

            String result = converter.convertToDatabaseColumn(locale);

            assertEquals("fr", result);
        }

        @Test
        @DisplayName("Should return null when locale is null")
        void testConvertNullLocale() {
            assertNull(converter.convertToDatabaseColumn(null));
        }
    }

    @Nested
    @DisplayName("convertToEntityAttribute")
    class ConvertToEntityAttribute {

        @Test
        @DisplayName("Should convert valid locale string with country to Locale")
        void testConvertValidLocaleStringWithCountry() {
            String dbData = "en-US";

            Locale locale = converter.convertToEntityAttribute(dbData);

            assertEquals("en", locale.getLanguage());
            assertEquals("US", locale.getCountry());
        }

        @Test
        @DisplayName("Should convert valid locale string without country")
        void testConvertValidLocaleStringWithoutCountry() {
            String dbData = "de";

            Locale locale = converter.convertToEntityAttribute(dbData);

            assertEquals("de", locale.getLanguage());
            assertTrue(locale.getCountry().isEmpty());
        }

        @Test
        @DisplayName("Should return null when dbData is null")
        void testConvertNullDbData() {
            assertNull(converter.convertToEntityAttribute(null));
        }

        @Test
        @DisplayName("Should return null when dbData is blank")
        void testConvertBlankDbData() {
            assertNull(converter.convertToEntityAttribute("   "));
        }
    }
}
