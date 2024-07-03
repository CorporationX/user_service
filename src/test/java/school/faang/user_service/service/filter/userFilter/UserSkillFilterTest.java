package school.faang.user_service.service.filter.userFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.userFilter.UserSkillFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserSkillFilterTest {
    private UserSkillFilter userSkillFilter;

    @BeforeEach
    void setUp() {
        userSkillFilter = new UserSkillFilter();
    }

    @Test
    public void testIsApplicable_withNonNullAboutPattern() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setSkillPattern("test");

        assertTrue(userSkillFilter.isApplicable(userFilterDto));
    }

    @Test
    public void testIsApplicable_withNullAboutPattern() {
        UserFilterDto userFilterDto = new UserFilterDto();

        assertFalse(userSkillFilter.isApplicable(userFilterDto));
    }

    @Test
    public void testApply_WithMatchingPattern() {
        Skill testSkill = mock(Skill.class);
        when(testSkill.getTitle()).thenReturn("test");
        Skill anotherSkill = mock(Skill.class);
        when(anotherSkill.getTitle()).thenReturn("another");

        UserFilterDto userFilterDto = mock(UserFilterDto.class);
        when(userFilterDto.getSkillPattern()).thenReturn("test");

        User firstUser = mock(User.class);
        List<Skill> firstSkills = new ArrayList<>();
        firstSkills.add(testSkill);
        when(firstUser.getSkills()).thenReturn(firstSkills);

        User secondUser = mock(User.class);
        List<Skill> secondSkills = new ArrayList<>();
        secondSkills.add(anotherSkill);
        when(secondUser.getSkills()).thenReturn(secondSkills);

        List<User> users = List.of(firstUser, secondUser);
        Stream<User> userStream = users.stream();

        userSkillFilter.apply(userStream, userFilterDto);

        List<User> filteredUsers = users.stream()
                .filter(user -> user.getSkills().stream()
                        .anyMatch(skill -> skill.getTitle().equals(userFilterDto.getSkillPattern())))
                .toList();

        assertEquals(1, filteredUsers.size());
        assertEquals("test", filteredUsers.get(0).getSkills().get(0).getTitle());
    }

    @Test
    public void testApply_WithNonMatchingPattern() {
        Skill testSkill = mock(Skill.class);
        when(testSkill.getTitle()).thenReturn("test");
        Skill anotherSkill = mock(Skill.class);
        when(anotherSkill.getTitle()).thenReturn("another");

        UserFilterDto userFilterDto = mock(UserFilterDto.class);
        when(userFilterDto.getSkillPattern()).thenReturn("nonmatching");

        User firstUser = mock(User.class);
        List<Skill> firstSkills = new ArrayList<>();
        firstSkills.add(testSkill);
        when(firstUser.getSkills()).thenReturn(firstSkills);

        User secondUser = mock(User.class);
        List<Skill> secondSkills = new ArrayList<>();
        secondSkills.add(anotherSkill);
        when(secondUser.getSkills()).thenReturn(secondSkills);

        List<User> users = List.of(firstUser, secondUser);
        Stream<User> userStream = users.stream();

        userSkillFilter.apply(userStream, userFilterDto);

        List<User> filteredUsers = users.stream()
                .filter(user -> user.getSkills().stream()
                        .anyMatch(skill -> skill.getTitle().equals(userFilterDto.getSkillPattern())))
                .toList();

        assertEquals(0, filteredUsers.size());
    }
}
