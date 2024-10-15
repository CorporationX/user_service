package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.model.filter_dto.UserFilterDto;
import school.faang.user_service.model.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PhoneFilterStrategyTest {

    private PhoneFilterStrategy phoneFilterStrategy;

    private static final String PHONE_MATCH = "12345";
    private static final String PHONE_NO_MATCH = "55555";
    private static final String PHONE_USER1 = "1234567890";
    private static final String PHONE_USER2 = "9876543210";
    private static final String USERNAME_1 = "user1";
    private static final String USERNAME_2 = "user2";

    @BeforeEach
    void setUp() {
        phoneFilterStrategy = new PhoneFilterStrategy();
    }

    private User createUser(long id, String username, String phone) {
        return User.builder()
                .id(id)
                .username(username)
                .phone(phone)
                .build();
    }

    private UserFilterDto createFilterWithPhonePattern(String pattern) {
        UserFilterDto filter = new UserFilterDto();
        filter.setPhonePattern(pattern);
        return filter;
    }

    @Test
    void testApplyFilter_WithNonEmptyPattern() {
        UserFilterDto filter = createFilterWithPhonePattern(PHONE_MATCH);
        assertTrue(phoneFilterStrategy.applyFilter(filter));
    }

    @Test
    void testApplyFilter_WithEmptyPattern() {
        UserFilterDto filter = createFilterWithPhonePattern("");
        assertFalse(phoneFilterStrategy.applyFilter(filter));
    }

    @Test
    void testApplyFilter_WithNullPattern() {
        UserFilterDto filter = createFilterWithPhonePattern(null);
        assertFalse(phoneFilterStrategy.applyFilter(filter));
    }

    @Test
    void testFilter_WithMatchingPattern() {
        User user1 = createUser(1L, USERNAME_1, PHONE_USER1);
        User user2 = createUser(2L, USERNAME_2, PHONE_USER2);

        List<User> users = List.of(user1, user2);
        UserFilterDto filter = createFilterWithPhonePattern(PHONE_MATCH);
        List<User> filteredUsers = phoneFilterStrategy.filter(users, filter);

        assertEquals(1, filteredUsers.size());
        assertEquals(USERNAME_1, filteredUsers.get(0).getUsername());
    }

    @Test
    void testFilter_WithNoMatchingPattern() {
        User user1 = createUser(1L, USERNAME_1, PHONE_USER1);
        User user2 = createUser(2L, USERNAME_2, PHONE_USER2);

        List<User> users = List.of(user1, user2);
        UserFilterDto filter = createFilterWithPhonePattern(PHONE_NO_MATCH);
        List<User> filteredUsers = phoneFilterStrategy.filter(users, filter);

        assertTrue(filteredUsers.isEmpty());
    }
}
