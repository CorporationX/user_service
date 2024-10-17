package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.model.filter_dto.UserFilterDto;
import school.faang.user_service.model.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailFilterStrategyTest {

    private EmailFilterStrategy emailFilterStrategy;

    private static final String EMAIL_MATCH = "test@example.com";
    private static final String EMAIL_NO_MATCH = "nomatch@example.com";
    private static final String EMAIL_OTHER = "other@example.com";
    private static final String USERNAME_1 = "user1";
    private static final String USERNAME_2 = "user2";

    @BeforeEach
    void setUp() {
        emailFilterStrategy = new EmailFilterStrategy();
    }

    private User createUser(long id, String username, String email) {
        return User.builder()
                .id(id)
                .username(username)
                .email(email)
                .build();
    }

    private UserFilterDto createFilterWithEmailPattern(String pattern) {
        UserFilterDto filter = new UserFilterDto();
        filter.setEmailPattern(pattern);
        return filter;
    }

    @Test
    void testApplyFilter_WithNonEmptyPattern() {
        UserFilterDto filter = createFilterWithEmailPattern(EMAIL_MATCH);
        assertTrue(emailFilterStrategy.applyFilter(filter));
    }

    @Test
    void testApplyFilter_WithEmptyPattern() {
        UserFilterDto filter = createFilterWithEmailPattern("");
        assertFalse(emailFilterStrategy.applyFilter(filter));
    }

    @Test
    void testApplyFilter_WithNullPattern() {
        UserFilterDto filter = createFilterWithEmailPattern(null);
        assertFalse(emailFilterStrategy.applyFilter(filter));
    }

    @Test
    void testFilter_WithMatchingPattern() {
        User user1 = createUser(1L, USERNAME_1, EMAIL_MATCH);
        User user2 = createUser(2L, USERNAME_2, EMAIL_OTHER);

        List<User> users = List.of(user1, user2);
        UserFilterDto filter = createFilterWithEmailPattern(EMAIL_MATCH);
        List<User> filteredUsers = emailFilterStrategy.filter(users, filter);

        assertEquals(1, filteredUsers.size());
        assertEquals(USERNAME_1, filteredUsers.get(0).getUsername());
    }

    @Test
    void testFilter_WithNoMatchingPattern() {
        User user1 = createUser(1L, USERNAME_1, EMAIL_MATCH);
        User user2 = createUser(2L, USERNAME_2, EMAIL_OTHER);

        List<User> users = List.of(user1, user2);
        UserFilterDto filter = createFilterWithEmailPattern(EMAIL_NO_MATCH);
        List<User> filteredUsers = emailFilterStrategy.filter(users, filter);

        assertTrue(filteredUsers.isEmpty());
    }
}