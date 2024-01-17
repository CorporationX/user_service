package school.faang.user_service.filter.user;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserEmailFilterTest {

    @Test
    void testIsApplicableWithPattern() {
        UserEmailFilter filter = new UserEmailFilter();
        UserFilterDto dto = new UserFilterDto();
        dto.setEmailPattern("example@mail.com");

        assertTrue(filter.isApplicable(dto));
    }

    @Test
    void testIsApplicableWithoutPattern() {
        UserEmailFilter filter = new UserEmailFilter();
        UserFilterDto dto = new UserFilterDto();

        assertFalse(filter.isApplicable(dto));
    }

    @Test
    void testApplyFilter() {
        UserEmailFilter filter = new UserEmailFilter();
        UserFilterDto dto = new UserFilterDto();
        dto.setEmailPattern("example@mail.com");

        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        Mockito.when(user1.getEmail()).thenReturn("user1@example@mail.com");
        Mockito.when(user2.getEmail()).thenReturn("user2@othermail.com");

        Stream<User> users = Stream.of(user1, user2);
        List<User> filteredUsers = filter.apply(users, dto).toList();

        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(user1));
        assertFalse(filteredUsers.contains(user2));
    }
}
