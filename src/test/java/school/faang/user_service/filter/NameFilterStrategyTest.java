package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.model.filter_dto.UserFilterDto;
import school.faang.user_service.model.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NameFilterStrategyTest {

    private NameFilterStrategy nameFilterStrategy;

    private static final String PATTERN_MATCH = "user";
    private static final String PATTERN_NO_MATCH = "guest";
    private static final String USERNAME_1 = "user1";
    private static final String USERNAME_2 = "admin";

    @BeforeEach
    void setUp() {
        nameFilterStrategy = new NameFilterStrategy();
    }

    private User createUser(long id, String username) {
        return User.builder()
                .id(id)
                .username(username)
                .build();
    }

    private UserFilterDto createFilterWithNamePattern(String pattern) {
        UserFilterDto filter = new UserFilterDto();
        filter.setNamePattern(pattern);
        return filter;
    }

    @Test
    void testApplyFilter_WithNonEmptyPattern() {
        UserFilterDto filter = createFilterWithNamePattern(PATTERN_MATCH);
        assertTrue(nameFilterStrategy.applyFilter(filter));
    }

    @Test
    void testApplyFilter_WithEmptyPattern() {
        UserFilterDto filter = createFilterWithNamePattern("");
        assertFalse(nameFilterStrategy.applyFilter(filter));
    }

    @Test
    void testApplyFilter_WithNullPattern() {
        UserFilterDto filter = createFilterWithNamePattern(null);
        assertFalse(nameFilterStrategy.applyFilter(filter));
    }

    @Test
    void testFilter_WithMatchingPattern() {
        User user1 = createUser(1L, USERNAME_1);
        User user2 = createUser(2L, USERNAME_2);

        List<User> users = List.of(user1, user2);
        UserFilterDto filter = createFilterWithNamePattern(PATTERN_MATCH);
        List<User> filteredUsers = nameFilterStrategy.filter(users, filter);

        assertEquals(1, filteredUsers.size());
        assertEquals(USERNAME_1, filteredUsers.get(0).getUsername());
    }

    @Test
    void testFilter_WithNoMatchingPattern() {
        User user1 = createUser(1L, USERNAME_1);
        User user2 = createUser(2L, USERNAME_2);

        List<User> users = List.of(user1, user2);
        UserFilterDto filter = createFilterWithNamePattern(PATTERN_NO_MATCH);
        List<User> filteredUsers = nameFilterStrategy.filter(users, filter);

        assertTrue(filteredUsers.isEmpty());
    }
}
