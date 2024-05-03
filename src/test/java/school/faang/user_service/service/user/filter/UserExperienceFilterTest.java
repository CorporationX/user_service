package school.faang.user_service.service.user.filter;

import org.junit.jupiter.api.BeforeEach;
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

    @Test
    void isApplicableForBothBoundsTest() {
        filter.setExperienceMin(3);
        filter.setExperienceMax(12);

        var isApplicable = userExperienceFilter.isApplicable(filter);

        assertTrue(isApplicable);
    }

    @Test
    void isApplicableForMinBoundOnlyTest() {
        filter.setExperienceMin(3);

        var isApplicable = userExperienceFilter.isApplicable(filter);

        assertTrue(isApplicable);
    }

    @Test
    void isApplicableForMaxBoundOnlyTest() {
        filter.setExperienceMax(3);

        var isApplicable = userExperienceFilter.isApplicable(filter);

        assertTrue(isApplicable);
    }

    @Test
    void isntApplicableForZeroExperiencePatternTest() {
        filter.setExperienceMin(0);
        filter.setExperienceMax(0);

        var isApplicable = userExperienceFilter.isApplicable(filter);

        assertFalse(isApplicable);
    }

    @Test
    void isntApplicableForOneNegativeExperienceBoundPatternTest() {
        filter.setExperienceMin(-10);
        filter.setExperienceMax(0);

        var isApplicable = userExperienceFilter.isApplicable(filter);

        assertFalse(isApplicable);
    }

    @Test
    void isntApplicableForNegativeExperiencePatternTest() {
        filter.setExperienceMin(-10);
        filter.setExperienceMax(-10);

        var isApplicable = userExperienceFilter.isApplicable(filter);

        assertFalse(isApplicable);
    }

    @Test
    void applyPositiveForBothBoundsTest() {
        filter.setExperienceMin(3);
        filter.setExperienceMax(12);

        var actualFilteredUsers = userExperienceFilter.apply(usersToFilter, filter);

        assertEquals(expectedFilteredUsers.toList(), actualFilteredUsers.toList());
    }

    @Test
    void applyPositiveForMinBoundOnlyTest() {
        filter.setExperienceMin(1);

        var actualFilteredUsers = userExperienceFilter.apply(usersToFilter, filter);

        assertEquals(ALL_USERS, actualFilteredUsers.toList());
    }

    @Test
    void applyPositiveForMaxBoundOnlyTest() {
        filter.setExperienceMax(4);
        expectedFilteredUsers = Stream.of(ALL_USERS.get(0), ALL_USERS.get(1));

        var actualFilteredUsers = userExperienceFilter.apply(usersToFilter, filter);

        assertEquals(expectedFilteredUsers.toList(), actualFilteredUsers.toList());
    }

    @Test
    void applyNonMatchingTest() {
        filter.setExperienceMin(12);
        filter.setExperienceMax(15);

        var actualFilteredUsers = userExperienceFilter.apply(usersToFilter, filter);

        assertEquals(List.of(), actualFilteredUsers.toList());
    }
}