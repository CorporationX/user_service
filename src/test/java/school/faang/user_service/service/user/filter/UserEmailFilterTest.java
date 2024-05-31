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

class UserEmailFilterTest {
    private final UserEmailFilter userEmailFilter = new UserEmailFilter();
    private UserFilterDto filter;
    private List<User> usersToFilter;
    private Stream<User> expectedFilteredUsers;

    @BeforeEach
    void setUp() {
        usersToFilter = ALL_USERS;

        filter = new UserFilterDto();
        filter.setEmailPattern("zenith@gmail.com");

        expectedFilteredUsers = Stream.of(ALL_USERS.get(2));
    }

    @Nested
    class PositiveTests {
        @DisplayName("should return true when \"emailPattern\" is present")
        @Test
        void shouldReturnTrueWhenEmailPatternIsPresent() {
            var isApplicable = userEmailFilter.isApplicable(filter);

            assertTrue(isApplicable);
        }

        @DisplayName("should return filtered by \"emailPattern\" users ")
        @Test
        void shouldReturnFilteredUsersWhenEmailPatternIsPresent() {
            var actualFilteredUsers = userEmailFilter.apply(usersToFilter, filter);

            assertEquals(expectedFilteredUsers.toList(), actualFilteredUsers.toList());
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should return false when \"emailPattern\" is empty")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        void shouldReturnFalseWhenEmailPatternIsEmpty(String pattern) {
            filter.setEmailPattern(pattern);

            var isApplicable = userEmailFilter.isApplicable(filter);

            assertFalse(isApplicable);
        }

        @DisplayName("should return empty list when no one user is matching filter")
        @Test
        void shouldReturnEmptyListWhenNothingMatch() {
            filter.setEmailPattern("dgdfg@mail.ru");

            var actualFilteredUsers = userEmailFilter.apply(usersToFilter, filter);

            assertEquals(List.of(), actualFilteredUsers.toList());
        }
    }
}