package school.faang.user_service.service.user.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static school.faang.user_service.service.user.filter.TestData.ALL_USERS;

class UserCityFilterTest {
    private final UserCityFilter userCityFilter = new UserCityFilter();
    private UserFilterDto filter;
    private Stream<User> usersToFilter;
    private Stream<User> expectedFilteredUsers;

    @BeforeEach
    void setUp() {
        usersToFilter = ALL_USERS.stream();

        filter = new UserFilterDto();
        filter.setCityPattern("Lon.*");

        expectedFilteredUsers = Stream.of(ALL_USERS.get(1));
    }

    @Nested
    class PositiveTests {
        @DisplayName("should return true when \"cityPattern\" is present")
        @Test
        void shouldReturnTrueWhenCityPatternIsPresent() {
            var isApplicable = userCityFilter.isApplicable(filter);

            assertTrue(isApplicable);
        }

        @DisplayName("should return filtered by \"cityPattern\" users ")
        @Test
        void shouldReturnFilteredUsersWhenCityFilterIsPresent() {
            var actualFilteredUsers = userCityFilter.apply(usersToFilter, filter);

            assertEquals(expectedFilteredUsers.toList(), actualFilteredUsers.toList());
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should return false when \"cityPattern\" is null")
        @Test
        void shouldReturnFalseWhenCityFilterIsNull() {
            filter.setCityPattern(null);

            var isApplicable = userCityFilter.isApplicable(filter);

            assertFalse(isApplicable);
        }

        @DisplayName("should return false when \"aboutPattern\" is blank")
        @Test
        void shouldReturnFalseWhenCityFilterIsBlank() {
            filter.setCityPattern("   ");

            var isApplicable = userCityFilter.isApplicable(filter);

            assertFalse(isApplicable);
        }

        @DisplayName("should return empty list when no one user is matching filter")
        @Test
        void shouldReturnEmptyListWhenNothingMatching() {
            filter.setCityPattern("Cahul");

            var actualFilteredUsers = userCityFilter.apply(usersToFilter, filter);

            assertEquals(List.of(), actualFilteredUsers.toList());
        }
    }
}