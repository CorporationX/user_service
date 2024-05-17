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

class UserAboutFilterTest {
    private final UserAboutFilter userAboutFilter = new UserAboutFilter();
    private UserFilterDto filter;
    private List<User> usersToFilter;
    private Stream<User> expectedFilteredUsers;

    @BeforeEach
    void setUp() {
        usersToFilter = ALL_USERS;

        filter = new UserFilterDto();
        filter.setAboutPattern("About zenith");

        expectedFilteredUsers = Stream.of(ALL_USERS.get(2));
    }

    @Nested
    class PositiveTests {
        @DisplayName("should return true when \"aboutPattern\" is present")
        @Test
        void shouldReturnTrueWhenAboutPatternIsPresent() {
            var isApplicable = userAboutFilter.isApplicable(filter);

            assertTrue(isApplicable);
        }

        @DisplayName("should return filtered by \"aboutPattern\" users ")
        @Test
        void shouldReturnFilteredUsersWhenAboutPatternIsPresent() {
            var actualFilteredUsers = userAboutFilter.apply(usersToFilter, filter);

            assertEquals(expectedFilteredUsers.toList(), actualFilteredUsers.toList());
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should return false when \"aboutPattern\" is empty")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        void shouldReturnFalseWhenAboutPatternIsEmpty(String pattern) {
            filter.setAboutPattern(pattern);

            var isApplicable = userAboutFilter.isApplicable(filter);

            assertFalse(isApplicable);
        }

        @DisplayName("should return empty list when no one user is matching filter")
        @Test
        void shouldReturnEmptyListWhenNothingMatch() {
            filter.setAboutPattern("About albert");

            var actualFilteredUsers = userAboutFilter.apply(usersToFilter, filter);

            assertEquals(List.of(), actualFilteredUsers.toList());
        }
    }
}