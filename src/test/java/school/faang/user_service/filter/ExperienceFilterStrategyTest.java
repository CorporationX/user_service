package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.model.filter_dto.UserFilterDto;
import school.faang.user_service.model.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExperienceFilterStrategyTest {

    private ExperienceFilterStrategy experienceFilterStrategy;

    private static final int EXPERIENCE_MIN_MATCH = 2;
    private static final int EXPERIENCE_MAX_MATCH = 5;
    private static final int EXPERIENCE_MIN_NO_MATCH = 7;
    private static final int EXPERIENCE_MAX_NO_MATCH = 10;
    private static final int EXPERIENCE_USER1 = 3;
    private static final int EXPERIENCE_USER2 = 6;
    private static final String USERNAME_1 = "user1";
    private static final String USERNAME_2 = "user2";

    @BeforeEach
    void setUp() {
        experienceFilterStrategy = new ExperienceFilterStrategy();
    }

    private User createUser(long id, String username, int experience) {
        return User.builder()
                .id(id)
                .username(username)
                .experience(experience)
                .build();
    }

    private UserFilterDto createFilterWithExperienceRange(int min, int max) {
        UserFilterDto filter = new UserFilterDto();
        filter.setExperienceMin(min);
        filter.setExperienceMax(max);
        return filter;
    }

    @Test
    void testApplyFilter_WithExperienceRange() {
        UserFilterDto filter = createFilterWithExperienceRange(EXPERIENCE_MIN_MATCH, EXPERIENCE_MAX_MATCH);
        assertTrue(experienceFilterStrategy.applyFilter(filter));
    }

    @Test
    void testApplyFilter_WithZeroExperienceRange() {
        UserFilterDto filter = createFilterWithExperienceRange(0, 0);
        assertFalse(experienceFilterStrategy.applyFilter(filter));
    }

    @Test
    void testFilter_WithMatchingExperience() {
        User user1 = createUser(1L, USERNAME_1, EXPERIENCE_USER1);
        User user2 = createUser(2L, USERNAME_2, EXPERIENCE_USER2);

        List<User> users = List.of(user1, user2);
        UserFilterDto filter = createFilterWithExperienceRange(EXPERIENCE_MIN_MATCH, EXPERIENCE_MAX_MATCH);
        List<User> filteredUsers = experienceFilterStrategy.filter(users, filter);

        assertEquals(1, filteredUsers.size());
        assertEquals(USERNAME_1, filteredUsers.get(0).getUsername());
    }

    @Test
    void testFilter_WithNoMatchingExperience() {
        User user1 = createUser(1L, USERNAME_1, EXPERIENCE_USER1);
        User user2 = createUser(2L, USERNAME_2, EXPERIENCE_USER2);

        List<User> users = List.of(user1, user2);
        UserFilterDto filter = createFilterWithExperienceRange(EXPERIENCE_MIN_NO_MATCH, EXPERIENCE_MAX_NO_MATCH);
        List<User> filteredUsers = experienceFilterStrategy.filter(users, filter);

        assertTrue(filteredUsers.isEmpty());
    }
}
