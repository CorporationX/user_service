package school.faang.user_service.mapper.user;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.entity.Country;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void testMapCountryToLocale_WithValidCountry_ShouldReturnLocale() {
        Locale correctResult = new Locale("fr", "FR");
        Country country = Country.builder()
                .localeCode("fr-FR")
                .build();

        Locale result = userMapper.mapCountryToLocale(country);

        assertNotNull(result);
        assertEquals(correctResult, result);
    }

    @Test
    void testMapCountryToLocale_WithNullCountry_ShouldReturnNull() {
        Locale locale = userMapper.mapCountryToLocale(null);

        assertNull(locale);
    }

    @Test
    void testMapCountryToLocale_WithNullLocaleCode_ShouldReturnNull() {
        Country country = Country.builder()
                .localeCode(null)
                .build();

        Locale locale = userMapper.mapCountryToLocale(country);

        assertNull(locale);
    }
}
