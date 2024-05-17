package school.faang.user_service.service.user.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static school.faang.user_service.service.user.filter.TestData.ALL_USERS;

class UserNameFilterTest {
    private final UserNameFilter userNameFilter = new UserNameFilter();
    private UserFilterDto filter;
    private List<User> usersToFilter;
    private Stream<User> expectedFilteredUsers;

    @BeforeEach
    void setUp() {
        usersToFilter = ALL_USERS;

        filter = new UserFilterDto();
        filter.setNamePattern("nadir");

        expectedFilteredUsers = Stream.of(ALL_USERS.get(0));
    }

    @Nested
    class PositiveTests {
        @DisplayName("should return true when \"namePattern\" is present")
        @Test
        void shouldReturnTrueWhenNamePatternIsPresent() {
            var isApplicable = userNameFilter.isApplicable(filter);

            assertTrue(isApplicable);
        }

        @DisplayName("should return filtered by \"namePattern\" users ")
        @Test
        void shouldReturnFilteredUsersWhenNamePatternIsPresent() {
            var actualFilteredUsers = userNameFilter.apply(usersToFilter, filter);

            assertEquals(expectedFilteredUsers.toList(), actualFilteredUsers.toList());
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should return false when \"namePattern\" is empty")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        void shouldReturnFalseWhenNamePatternIsEmpty(String pattern) {
            filter.setNamePattern(pattern);

            var isApplicable = userNameFilter.isApplicable(filter);

            assertFalse(isApplicable);
        }

        @DisplayName("should return empty list when no one user is matching filter")
        @Test
        void shouldReturnEmptyListWhenNothingMatch() {
            filter.setNamePattern("Albert");

            var actualFilteredUsers = userNameFilter.apply(usersToFilter, filter);

            assertEquals(List.of(), actualFilteredUsers.toList());
        }
    }
}