package school.faang.user_service.filter.user;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;


import java.util.List;

class SkillFilterTest {
    private User userFirst;
    private User userSecond;
    private User userThird;
    private Skill skillFirst;
    private Skill skillSecond;
    private SkillFilter skillFilter;
    private UserFilterDto userFilterDto;

    @BeforeEach
    void setup() {
        skillFirst = new Skill();
        skillSecond = new Skill();
        userFirst = new User();
        userSecond = new User();
        userThird = new User();
        skillFilter = new SkillFilter();
        userFilterDto = mock(UserFilterDto.class);
    }

    @Test
    void testIsApplicableWhenSkillPatternIsNotNull() {
        when(userFilterDto.getSkillPattern()).thenReturn("PHP");
        assertTrue(skillFilter.isApplicable(userFilterDto));
    }

    @Test
    void testIsApplicableWhenSkillPatternIsNull() {
        when(userFilterDto.getSkillPattern()).thenReturn(null);
        assertFalse(skillFilter.isApplicable(userFilterDto));
    }

    @Test
    void testApplyWhenUsersMatchSkillPattern() {
        List<User> filteredUsers = getUserList();
        when(userFilterDto.getSkillPattern()).thenReturn("Java");

        filteredUsers = skillFilter.apply(filteredUsers.stream(), userFilterDto).toList();
        assertEquals(2, filteredUsers.size());
        assertTrue(filteredUsers.contains(filteredUsers.get(0)));
        assertTrue(filteredUsers.contains(filteredUsers.get(1)));
    }

    @Test
    void testApplyWhenNoUsersMatchSkillPattern() {
        List<User> filteredUsers = getUserList();
        when(userFilterDto.getSkillPattern()).thenReturn("GO");

        filteredUsers = skillFilter.apply(filteredUsers.stream(), userFilterDto).toList();
        assertEquals(0, filteredUsers.size());
        assertTrue(filteredUsers.isEmpty());
    }

    List<User> getUserList() {
        skillFirst.setTitle("Java");
        skillSecond.setTitle("Python");
        userFirst.setSkills(List.of(skillFirst, skillSecond));
        userSecond.setSkills(List.of(skillFirst));
        userThird.setSkills(List.of(skillSecond));
        return List.of(userFirst, userSecond, userThird);
    }
}