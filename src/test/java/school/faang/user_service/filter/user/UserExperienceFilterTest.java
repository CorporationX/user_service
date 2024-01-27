package school.faang.user_service.filter.user;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserExperienceFilterTest {

    @Test
    void testIsApplicableWithMinMax() {
        UserExperienceFilter filter = new UserExperienceFilter();
        UserFilterDto dto = new UserFilterDto();
        dto.setExperienceMin(5);
        dto.setExperienceMax(10);

        assertTrue(filter.isApplicable(dto));
    }

    @Test
    void testIsApplicableWithoutMinMax() {
        UserExperienceFilter filter = new UserExperienceFilter();
        UserFilterDto dto = new UserFilterDto();

        assertFalse(filter.isApplicable(dto));
    }

    @Test
    void testApplyFilter() {
        UserExperienceFilter filter = new UserExperienceFilter();
        UserFilterDto dto = new UserFilterDto();
        dto.setExperienceMin(5);
        dto.setExperienceMax(10);

        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        Mockito.when(user1.getExperience()).thenReturn(7);
        Mockito.when(user2.getExperience()).thenReturn(11);

        Stream<User> users = Stream.of(user1, user2);
        List<User> filteredUsers = filter.apply(users, dto).toList();

        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(user1));
        assertFalse(filteredUsers.contains(user2));
    }
}
