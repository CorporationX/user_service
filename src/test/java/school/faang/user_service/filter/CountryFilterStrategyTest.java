package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.model.filter_dto.UserFilterDto;
import school.faang.user_service.model.entity.Country;
import school.faang.user_service.model.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CountryFilterStrategyTest {

    private CountryFilterStrategy countryFilterStrategy;

    private static final String RUSSIA = "Russia";
    private static final String USA = "USA";
    private static final String GERMANY = "Germany";
    private static final String USERNAME_1 = "user1";
    private static final String USERNAME_2 = "user2";

    @BeforeEach
    void setUp() {
        countryFilterStrategy = new CountryFilterStrategy();
    }

    private Country createCountry(long id, String title) {
        return Country.builder()
                .id(id)
                .title(title)
                .build();
    }

    private User createUser(long id, String username, Country country) {
        return User.builder()
                .id(id)
                .username(username)
                .country(country)
                .build();
    }

    private UserFilterDto createFilterWithCountryPattern(String pattern) {
        UserFilterDto filter = new UserFilterDto();
        filter.setCountryPattern(pattern);
        return filter;
    }

    @Test
    void testApplyFilter_WithNonEmptyPattern() {
        UserFilterDto filter = createFilterWithCountryPattern(RUSSIA);
        assertTrue(countryFilterStrategy.applyFilter(filter));
    }

    @Test
    void testApplyFilter_WithEmptyPattern() {
        UserFilterDto filter = createFilterWithCountryPattern("");
        assertFalse(countryFilterStrategy.applyFilter(filter));
    }

    @Test
    void testApplyFilter_WithNullPattern() {
        UserFilterDto filter = createFilterWithCountryPattern(null);
        assertFalse(countryFilterStrategy.applyFilter(filter));
    }

    @Test
    void testFilter_WithMatchingPattern() {
        Country country1 = createCountry(1L, RUSSIA);
        Country country2 = createCountry(2L, USA);

        User user1 = createUser(1L, USERNAME_1, country1);
        User user2 = createUser(2L, USERNAME_2, country2);

        List<User> users = List.of(user1, user2);

        UserFilterDto filter = createFilterWithCountryPattern(RUSSIA);
        List<User> filteredUsers = countryFilterStrategy.filter(users, filter);

        assertEquals(1, filteredUsers.size());
        assertEquals(USERNAME_1, filteredUsers.get(0).getUsername());
    }

    @Test
    void testFilter_WithNoMatchingPattern() {
        Country country1 = createCountry(1L, RUSSIA);
        Country country2 = createCountry(2L, USA);

        User user1 = createUser(1L, USERNAME_1, country1);
        User user2 = createUser(2L, USERNAME_2, country2);

        List<User> users = List.of(user1, user2);

        UserFilterDto filter = createFilterWithCountryPattern(GERMANY);
        List<User> filteredUsers = countryFilterStrategy.filter(users, filter);

        assertTrue(filteredUsers.isEmpty());
    }
}
