package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.model.filter_dto.UserFilterDto;
import school.faang.user_service.model.entity.Skill;
import school.faang.user_service.model.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SkillFilterStrategyTest {

    private SkillFilterStrategy skillFilterStrategy;

    private static final String SKILL_JAVA = "Java";
    private static final String SKILL_PYTHON = "Python";
    private static final String SKILL_NO_MATCH = "C++";
    private static final String USERNAME_1 = "user1";
    private static final String USERNAME_2 = "user2";

    @BeforeEach
    void setUp() {
        skillFilterStrategy = new SkillFilterStrategy();
    }

    private Skill createSkill(long id, String title) {
        return Skill.builder()
                .id(id)
                .title(title)
                .build();
    }

    private User createUser(long id, String username, List<Skill> skills) {
        return User.builder()
                .id(id)
                .username(username)
                .skills(skills)
                .build();
    }

    private UserFilterDto createFilterWithSkillPattern(String pattern) {
        UserFilterDto filter = new UserFilterDto();
        filter.setSkillPattern(pattern);
        return filter;
    }

    @Test
    void testApplyFilter_WithNonEmptyPattern() {
        UserFilterDto filter = createFilterWithSkillPattern(SKILL_JAVA);
        assertTrue(skillFilterStrategy.applyFilter(filter));
    }

    @Test
    void testApplyFilter_WithEmptyPattern() {
        UserFilterDto filter = createFilterWithSkillPattern("");
        assertFalse(skillFilterStrategy.applyFilter(filter));
    }

    @Test
    void testApplyFilter_WithNullPattern() {
        UserFilterDto filter = createFilterWithSkillPattern(null);
        assertFalse(skillFilterStrategy.applyFilter(filter));
    }

    @Test
    void testFilter_WithMatchingSkill() {
        Skill skill1 = createSkill(1L, SKILL_JAVA);
        Skill skill2 = createSkill(2L, SKILL_PYTHON);

        User user1 = createUser(1L, USERNAME_1, List.of(skill1));
        User user2 = createUser(2L, USERNAME_2, List.of(skill2));

        List<User> users = List.of(user1, user2);
        UserFilterDto filter = createFilterWithSkillPattern(SKILL_JAVA);
        List<User> filteredUsers = skillFilterStrategy.filter(users, filter);

        assertEquals(1, filteredUsers.size());
        assertEquals(USERNAME_1, filteredUsers.get(0).getUsername());
    }

    @Test
    void testFilter_WithNoMatchingSkill() {
        Skill skill1 = createSkill(1L, SKILL_JAVA);
        Skill skill2 = createSkill(2L, SKILL_PYTHON);

        User user1 = createUser(1L, USERNAME_1, List.of(skill1));
        User user2 = createUser(2L, USERNAME_2, List.of(skill2));

        List<User> users = List.of(user1, user2);
        UserFilterDto filter = createFilterWithSkillPattern(SKILL_NO_MATCH);
        List<User> filteredUsers = skillFilterStrategy.filter(users, filter);

        assertTrue(filteredUsers.isEmpty());
    }
}
