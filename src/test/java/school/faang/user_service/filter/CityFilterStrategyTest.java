package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.model.filter_dto.UserFilterDto;
import school.faang.user_service.model.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CityFilterStrategyTest {

    private CityFilterStrategy cityFilterStrategy;

    private static final String NEW_YORK = "New York";
    private static final String LOS_ANGELES = "Los Angeles";
    private static final String CHICAGO = "Chicago";
    private static final String USERNAME_1 = "user1";
    private static final String USERNAME_2 = "user2";

    @BeforeEach
    void setUp() {
        cityFilterStrategy = new CityFilterStrategy();
    }

    private User createUser(long id, String username, String city) {
        return User.builder()
                .id(id)
                .username(username)
                .city(city)
                .build();
    }

    private UserFilterDto createFilterWithCityPattern(String pattern) {
        UserFilterDto filter = new UserFilterDto();
        filter.setCityPattern(pattern);
        return filter;
    }

    @Test
    void testApplyFilter_WithNonEmptyPattern() {
        UserFilterDto filter = createFilterWithCityPattern(NEW_YORK);
        assertTrue(cityFilterStrategy.applyFilter(filter));
    }

    @Test
    void testApplyFilter_WithEmptyPattern() {
        UserFilterDto filter = createFilterWithCityPattern("");
        assertFalse(cityFilterStrategy.applyFilter(filter));
    }

    @Test
    void testApplyFilter_WithNullPattern() {
        UserFilterDto filter = createFilterWithCityPattern(null);
        assertFalse(cityFilterStrategy.applyFilter(filter));
    }

    @Test
    void testFilter_WithMatchingPattern() {
        List<User> users = List.of(
                createUser(1L, USERNAME_1, NEW_YORK),
                createUser(2L, USERNAME_2, LOS_ANGELES)
        );
        UserFilterDto filter = createFilterWithCityPattern(NEW_YORK);
        List<User> filteredUsers = cityFilterStrategy.filter(users, filter);

        assertEquals(1, filteredUsers.size());
        assertEquals(USERNAME_1, filteredUsers.get(0).getUsername());
    }

    @Test
    void testFilter_WithNoMatchingPattern() {
        List<User> users = List.of(
                createUser(1L, USERNAME_1, NEW_YORK),
                createUser(2L, USERNAME_2, LOS_ANGELES)
        );
        UserFilterDto filter = createFilterWithCityPattern(CHICAGO);
        List<User> filteredUsers = cityFilterStrategy.filter(users, filter);

        assertTrue(filteredUsers.isEmpty());
    }
}

