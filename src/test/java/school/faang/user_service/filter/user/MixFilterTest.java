package school.faang.user_service.filter.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.List;

public class MixFilterTest {
    private User userFirst;
    private User userSecond;
    private User userThird;
    private Skill skillFirst;
    private Skill skillSecond;
    private SkillFilter skillFilter;
    private PhoneFilter phoneFilter;
    private UserFilterDto userFilterDto;

    @BeforeEach
    void setup() {
        skillFirst = new Skill();
        skillSecond = new Skill();
        userFirst = new User();
        userSecond = new User();
        userThird = new User();
        skillFilter = new SkillFilter();
        phoneFilter = new PhoneFilter();
        userFilterDto = mock(UserFilterDto.class);
    }

    @Test
    void testApplyWhenUsersMatchMixFilters() {
        List<User> filteredUsers = getUserList();
        when(userFilterDto.getSkillPattern()).thenReturn("Java");
        when(userFilterDto.getPhonePattern()).thenReturn("+854521475544");

        filteredUsers = skillFilter.apply(filteredUsers.stream(), userFilterDto).toList();
        filteredUsers = phoneFilter.apply(filteredUsers.stream(), userFilterDto).toList();
        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(filteredUsers.get(0)));
    }

    @Test
    void testApplyWhenNoUsersMatchMixFilters() {
        List<User> filteredUsers = getUserList();
        when(userFilterDto.getSkillPattern()).thenReturn("GO");
        when(userFilterDto.getPhonePattern()).thenReturn("+995665491");

        filteredUsers = skillFilter.apply(filteredUsers.stream(), userFilterDto).toList();
        filteredUsers = phoneFilter.apply(filteredUsers.stream(), userFilterDto).toList();
        assertEquals(0, filteredUsers.size());
        assertTrue(filteredUsers.isEmpty());
    }

    List<User> getUserList() {
        skillFirst.setTitle("Java");
        skillSecond.setTitle("Python");
        userFirst.setSkills(List.of(skillFirst, skillSecond));
        userSecond.setSkills(List.of(skillFirst));
        userThird.setSkills(List.of(skillSecond));
        userFirst.setPhone("+854521475544");
        userSecond.setPhone("+78468222");
        userThird.setPhone("+78545236528");
        return List.of(userFirst, userSecond, userThird);
    }
}
