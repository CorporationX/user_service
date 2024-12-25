package school.faang.user_service.filter.userFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.model.Skill;
import school.faang.user_service.model.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserFilterTest {

    private List<UserFilter> filters;

    @BeforeEach
    public void setUp() {
        filters = Arrays.asList(new AboutPatternFilter(), new SkillsPatternFilter());
    }

    @Test
    public void testApplyAboutFilter() {
        AboutPatternFilter filter = new AboutPatternFilter();
        User user = new User();
        user.setAboutMe("I love coding");

        UserFilterDto filterDto = UserFilterDto.builder()
                .aboutPattern("coding")
                .build();

        assertTrue(filter.isApplicable(filterDto));
        assertTrue(filter.apply(user));

        filterDto = UserFilterDto.builder()
                .aboutPattern("dancing")
                .build();

        assertTrue(filter.isApplicable(filterDto));
        assertFalse(filter.apply(user));
    }

    @Test
    public void testApplySkillFilter() {
        SkillsPatternFilter filter = new SkillsPatternFilter();
        User user = new User();
        Skill skill = new Skill();
        skill.setTitle("Java");
        user.setSkills(Collections.singletonList(skill));

        UserFilterDto filterDto = UserFilterDto.builder()
                .skillPattern("Java")
                .build();

        assertTrue(filter.isApplicable(filterDto));
        assertTrue(filter.apply(user));

        filterDto = UserFilterDto.builder()
                .skillPattern("Python")
                .build();

        assertTrue(filter.isApplicable(filterDto));
        assertFalse(filter.apply(user));
    }

    @Test
    public void testApplyMinExperienceFilter() {
        MinExperiencePatternFilter filter = new MinExperiencePatternFilter();
        User user = new User();
        user.setExperience(5);

        UserFilterDto filterDto = UserFilterDto.builder()
                .experienceMin(5)
                .build();

        assertTrue(filter.isApplicable(filterDto));
        assertTrue(filter.apply(user));

        filterDto = UserFilterDto.builder()
                .experienceMin(6)
                .build();

        assertTrue(filter.isApplicable(filterDto));
        assertFalse(filter.apply(user));
    }

    // Остальные фильтры работают по той же логике, что и фильтры выше

    @Test
    public void testApplyFilters() {
        User user1 = new User();
        user1.setAboutMe("I love coding");
        Skill skill1 = new Skill();
        skill1.setTitle("Java");
        user1.setSkills(Collections.singletonList(skill1));

        User user2 = new User();
        user2.setAboutMe("I love dancing");
        Skill skill2 = new Skill();
        skill2.setTitle("Python");
        user2.setSkills(Collections.singletonList(skill2));

        List<User> users = Arrays.asList(user1, user2);

        UserFilterDto filterDto = UserFilterDto.builder()
                .aboutPattern("coding")
                .skillPattern("Java")
                .build();

        List<User> filteredUsers = users.stream()
                .filter(user -> filters.stream()
                        .filter(filter -> filter.isApplicable(filterDto))
                        .allMatch(filter -> filter.apply(user)))
                .toList();

        assertEquals(1, filteredUsers.size());
        assertEquals(user1, filteredUsers.get(0));
    }
}
