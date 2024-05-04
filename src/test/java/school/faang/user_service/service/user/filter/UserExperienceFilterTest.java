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

class UserExperienceFilterTest {
    private final UserExperienceFilter userExperienceFilter = new UserExperienceFilter();
    private UserFilterDto filter;
    private Stream<User> usersToFilter;
    private Stream<User> expectedFilteredUsers;

    @BeforeEach
    void setUp() {
        usersToFilter = ALL_USERS.stream();

        filter = new UserFilterDto();

        expectedFilteredUsers = Stream.of(ALL_USERS.get(0), ALL_USERS.get(2));
    }

    @Nested
    class PositiveTests {
        @DisplayName("should return true when both experience bounds are present")
        @Test
        void shouldReturnTrueWhenBothExperienceBoundsArePresent() {
            filter.setExperienceMin(3);
            filter.setExperienceMax(12);

            var isApplicable = userExperienceFilter.isApplicable(filter);

            assertTrue(isApplicable);
        }

        @DisplayName("should return true when only min bound is present")
        @Test
        void shouldReturnTrueWhenMinBoundOnlyIsPresent() {
            filter.setExperienceMin(3);

            var isApplicable = userExperienceFilter.isApplicable(filter);

            assertTrue(isApplicable);
        }

        @DisplayName("should return true when only max bound is present")
        @Test
        void shouldReturnTrueWhenMaxBoundOnlyIsPresent() {
            filter.setExperienceMax(3);

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
    }

    @Nested
    class NegativeTests {
        @DisplayName("should return false when both bounds are zero")
        @Test
        void shouldReturnFalseWhenBothBoundsAreZero() {
            filter.setExperienceMin(0);
            filter.setExperienceMax(0);

            var isApplicable = userExperienceFilter.isApplicable(filter);

            assertFalse(isApplicable);
        }

        @DisplayName("should return false when one of bounds is negative")
        @Test
        void shouldReturnFalseWhenOneBoundIsNegative() {
            filter.setExperienceMin(-10);
            filter.setExperienceMax(0);

            var isApplicable = userExperienceFilter.isApplicable(filter);

            assertFalse(isApplicable);
        }

        @DisplayName("should return false when both bounds are negative")
        @Test
        void shouldReturnFalseWhenBothBoundsAreNegative() {
            filter.setExperienceMin(-10);
            filter.setExperienceMax(-10);

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
    }
}