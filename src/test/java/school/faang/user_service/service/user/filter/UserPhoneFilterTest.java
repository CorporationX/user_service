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

class UserPhoneFilterTest {
    private final UserPhoneFilter userPhoneFilter = new UserPhoneFilter();
    private UserFilterDto filter;
    private Stream<User> usersToFilter;
    private Stream<User> expectedFilteredUsers;

    @BeforeEach
    void setUp() {
        usersToFilter = ALL_USERS.stream();

        filter = new UserFilterDto();
        filter.setPhonePattern("123..");

        expectedFilteredUsers = Stream.of(ALL_USERS.get(0));
    }

    @Test
    void isApplicablePositiveTest() {
        var isApplicable = userPhoneFilter.isApplicable(filter);

        assertTrue(isApplicable);
    }

    @Test
    void isApplicableForNullPatternTest() {
        filter.setPhonePattern(null);

        var isApplicable = userPhoneFilter.isApplicable(filter);

        assertFalse(isApplicable);
    }

    @Test
    void isApplicableForBlankPatternTest() {
        filter.setPhonePattern("   ");

        var isApplicable = userPhoneFilter.isApplicable(filter);

        assertFalse(isApplicable);
    }

    @Test
    void applyPositiveTest() {
        var actualFilteredUsers = userPhoneFilter.apply(usersToFilter, filter);

        assertEquals(expectedFilteredUsers.toList(), actualFilteredUsers.toList());
    }

    @Test
    void applyNonMatchingTest() {
        filter.setPhonePattern("dfgdfg");

        var actualFilteredUsers = userPhoneFilter.apply(usersToFilter, filter);

        assertEquals(List.of(), actualFilteredUsers.toList());
    }
}