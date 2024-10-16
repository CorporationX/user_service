package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.model.filter_dto.UserFilterDto;
import school.faang.user_service.model.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AboutFilterStrategyTest {

    private AboutFilterStrategy aboutFilterStrategy;

    private static final String DEVELOPER = "developer";
    private static final String SOFTWARE_DEVELOPER_ABOUT = "I am a software developer.";
    private static final String DATA_SCIENTIST_ABOUT = "I am a data scientist.";
    private static final String USERNAME_1 = "user1";
    private static final String USERNAME_2 = "user2";

    @BeforeEach
    void setUp() {
        aboutFilterStrategy = new AboutFilterStrategy();
    }

    private User createUser(long id, String username, String aboutMe) {
        return User.builder()
                .id(id)
                .username(username)
                .aboutMe(aboutMe)
                .build();
    }

    private UserFilterDto createFilterWithAboutPattern(String pattern) {
        UserFilterDto filter = new UserFilterDto();
        filter.setAboutPattern(pattern);
        return filter;
    }

    @Test
    void testApplyFilter_WithNonEmptyPattern() {
        UserFilterDto filter = createFilterWithAboutPattern(DEVELOPER);
        assertTrue(aboutFilterStrategy.applyFilter(filter));
    }

    @Test
    void testApplyFilter_WithEmptyPattern() {
        UserFilterDto filter = createFilterWithAboutPattern("");
        assertFalse(aboutFilterStrategy.applyFilter(filter));
    }

    @Test
    void testApplyFilter_WithNullPattern() {
        UserFilterDto filter = createFilterWithAboutPattern(null);
        assertFalse(aboutFilterStrategy.applyFilter(filter));
    }

    @Test
    void testFilter_WithMatchingPattern() {
        List<User> users = List.of(
                createUser(1L, USERNAME_1, SOFTWARE_DEVELOPER_ABOUT),
                createUser(2L, USERNAME_2, DATA_SCIENTIST_ABOUT)
        );

        UserFilterDto filter = createFilterWithAboutPattern(DEVELOPER);
        List<User> filteredUsers = aboutFilterStrategy.filter(users, filter);

        assertEquals(1, filteredUsers.size());
        assertEquals(USERNAME_1, filteredUsers.get(0).getUsername());
    }

    @Test
    void testFilter_WithNoMatchingPattern() {
        List<User> users = List.of(
                createUser(1L, USERNAME_1, SOFTWARE_DEVELOPER_ABOUT),
                createUser(2L, USERNAME_2, DATA_SCIENTIST_ABOUT)
        );

        UserFilterDto filter = createFilterWithAboutPattern("manager");
        List<User> filteredUsers = aboutFilterStrategy.filter(users, filter);

        assertTrue(filteredUsers.isEmpty());
    }
}