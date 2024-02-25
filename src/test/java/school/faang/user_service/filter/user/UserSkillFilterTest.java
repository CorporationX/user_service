package school.faang.user_service.filter.user;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserSkillFilterTest {

    @Test
    void testIsApplicableWithPattern() {
        UserSkillFilter filter = new UserSkillFilter();
        UserFilterDto dto = new UserFilterDto();
        dto.setSkillPattern("Java");

        assertTrue(filter.isApplicable(dto));
    }

    @Test
    void testIsApplicableWithoutPattern() {
        UserSkillFilter filter = new UserSkillFilter();
        UserFilterDto dto = new UserFilterDto();

        assertFalse(filter.isApplicable(dto));
    }

    @Test
    void testApplyFilter() {
        UserSkillFilter filter = new UserSkillFilter();
        UserFilterDto dto = new UserFilterDto();
        dto.setSkillPattern("Java");

        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        Skill skill1 = new Skill();
        Skill skill2 = new Skill();
        skill1.setTitle("Java");
        skill2.setTitle("Python");
        Mockito.when(user1.getSkills()).thenReturn(List.of(skill1, skill2));
        Mockito.when(user2.getSkills()).thenReturn(List.of(skill2));

        Stream<User> users = Stream.of(user1, user2);
        List<User> filteredUsers = filter.apply(users, dto).toList();

        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(user1));
        assertFalse(filteredUsers.contains(user2));
    }
}
