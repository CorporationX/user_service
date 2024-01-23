package school.faang.user_service.filter.user;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserAboutFilterTest {

    @Test
    void testIsApplicableWithPattern() {
        UserAboutFilter filter = new UserAboutFilter();
        UserFilterDto dto = new UserFilterDto();
        dto.setAboutPattern("разработчик");

        assertTrue(filter.isApplicable(dto));
    }

    @Test
    void testIsApplicableWithoutPattern() {
        UserAboutFilter filter = new UserAboutFilter();
        UserFilterDto dto = new UserFilterDto();

        assertFalse(filter.isApplicable(dto));
    }

    @Test
    void testApplyFilter() {
        UserAboutFilter filter = new UserAboutFilter();
        UserFilterDto dto = new UserFilterDto();
        dto.setAboutPattern("разработчик");

        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        Mockito.when(user1.getAboutMe()).thenReturn("Опытный разработчик");
        Mockito.when(user2.getAboutMe()).thenReturn("Художник");

        Stream<User> users = Stream.of(user1, user2);
        List<User> filteredUsers = filter.apply(users, dto).toList();

        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(user1));
        assertFalse(filteredUsers.contains(user2));
    }
}
