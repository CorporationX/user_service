package school.faang.user_service.service.user.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static school.faang.user_service.service.user.filter.TestData.ALL_USERS;

class UserExperienceFilterTest {
    private final UserExperienceFilter userExperienceFilter = new UserExperienceFilter();
    private UserFilterDto filter;
    private List<User> usersToFilter;
    private Stream<User> expectedFilteredUsers;

    @BeforeEach
    void setUp() {
        usersToFilter = ALL_USERS;

        filter = new UserFilterDto();

        expectedFilteredUsers = Stream.of(ALL_USERS.get(0), ALL_USERS.get(2));
    }

    @Nested
    class PositiveTests {
        @DisplayName("should return true when experience bounds are present")
        @ParameterizedTest
        @MethodSource("provideExpBoundsForPositiveTest")
        void shouldReturnTrueWhenExperienceBoundsArePresent(int minBound, int maxBound) {
            filter.setExperienceMin(minBound);
            filter.setExperienceMax(maxBound);

            var isApplicable = userExperienceFilter.isApplicable(filter);

            assertTrue(isApplicable);
        }

        @DisplayName("should return filtered users when both experience bounds are present")
        @Test
        void shouldReturnFilteredUsersWhenBothExperienceBoundsArePresent() {
            filter.setExperienceMin(3);
            filter.setExperienceMax(12);

            var actualFilteredUsers = userExperienceFilter.apply(usersToFilter, filter);

            assertEquals(expectedFilteredUsers.toList(), actualFilteredUsers.toList());
        }

        @DisplayName("should return filtered users when min bound only is present")
        @Test
        void shouldReturnFilteredUsersWhenMinBoundOnlyIsPresent() {
            filter.setExperienceMin(1);

            var actualFilteredUsers = userExperienceFilter.apply(usersToFilter, filter);

            assertEquals(ALL_USERS, actualFilteredUsers.toList());
        }

        @DisplayName("should return filtered users when max bound only is present")
        @Test
        void shouldReturnFilteredUsersWhenMaxBoundOnlyIsPresent() {
            filter.setExperienceMax(4);
            expectedFilteredUsers = Stream.of(ALL_USERS.get(0), ALL_USERS.get(1));

            var actualFilteredUsers = userExperienceFilter.apply(usersToFilter, filter);

            assertEquals(expectedFilteredUsers.toList(), actualFilteredUsers.toList());
        }

        private static Stream<Arguments> provideExpBoundsForPositiveTest() {
            return Stream.of(
                    Arguments.of(10, 10),
                    Arguments.of(10, 0),
                    Arguments.of(0, 10)
            );
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should return false when experience bounds are empty or negative")
        @ParameterizedTest
        @MethodSource("provideExpBoundsForNegativeTest")
        void shouldReturnFalseWhenBoundsAreEmptyOrNegative(int minBound, int maxBound) {
            filter.setExperienceMin(minBound);
            filter.setExperienceMax(maxBound);

            var isApplicable = userExperienceFilter.isApplicable(filter);

            assertFalse(isApplicable);
        }

        @DisplayName("should return empty list when no one user is matching filter")
        @Test
        void shouldReturnEmptyListWhenNothingMatch() {
            filter.setExperienceMin(12);
            filter.setExperienceMax(15);

            var actualFilteredUsers = userExperienceFilter.apply(usersToFilter, filter);

            assertEquals(List.of(), actualFilteredUsers.toList());
        }

        private static Stream<Arguments> provideExpBoundsForNegativeTest() {
            return Stream.of(
                    Arguments.of(-10, -10),
                    Arguments.of(-10, 0),
                    Arguments.of(0, -10),
                    Arguments.of(0, 0),
                    Arguments.of(10, -10),
                    Arguments.of(-10, 10)
            );
        }
    }
}