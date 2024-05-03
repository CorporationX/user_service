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

class UserAboutFilterTest {
    private final UserAboutFilter userAboutFilter = new UserAboutFilter();
    private UserFilterDto filter;
    private Stream<User> usersToFilter;
    private Stream<User> expectedFilteredUsers;

    @BeforeEach
    void setUp() {
        usersToFilter = ALL_USERS.stream();

        filter = new UserFilterDto();
        filter.setAboutPattern("About zenith");

        expectedFilteredUsers = Stream.of(ALL_USERS.get(2));
    }

    @Test
    void isApplicablePositiveTest() {
        var isApplicable = userAboutFilter.isApplicable(filter);

        assertTrue(isApplicable);
    }

    @Test
    void isApplicableForNullPatternTest() {
        filter.setAboutPattern(null);

        var isApplicable = userAboutFilter.isApplicable(filter);

        assertFalse(isApplicable);
    }

    @Test
    void isApplicableForBlankPatternTest() {
        filter.setAboutPattern("   ");

        var isApplicable = userAboutFilter.isApplicable(filter);

        assertFalse(isApplicable);
    }

    @Test
    void applyPositiveTest() {
        var actualFilteredUsers = userAboutFilter.apply(usersToFilter, filter);

        assertEquals(expectedFilteredUsers.toList(), actualFilteredUsers.toList());
    }

    @Test
    void applyNonMatchingTest() {
        filter.setAboutPattern("About albert");

        var actualFilteredUsers = userAboutFilter.apply(usersToFilter, filter);

        assertEquals(List.of(), actualFilteredUsers.toList());
    }
}