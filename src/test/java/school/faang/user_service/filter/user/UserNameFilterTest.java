package school.faang.user_service.filter.user;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserNameFilterTest {

    @Test
    void testIsApplicableWithPattern() {
        UserNameFilter filter = new UserNameFilter();
        UserFilterDto dto = new UserFilterDto();
        dto.setNamePattern("John");

        assertTrue(filter.isApplicable(dto));
    }

    @Test
    void testIsApplicableWithoutPattern() {
        UserNameFilter filter = new UserNameFilter();
        UserFilterDto dto = new UserFilterDto();

        assertFalse(filter.isApplicable(dto));
    }

    @Test
    void testApplyFilter() {
        UserNameFilter filter = new UserNameFilter();
        UserFilterDto dto = new UserFilterDto();
        dto.setNamePattern("John");

        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        Mockito.when(user1.getUsername()).thenReturn("John");
        Mockito.when(user2.getUsername()).thenReturn("Bob");

        Stream<User> users = Stream.of(user1, user2);
        List<User> filteredUsers = filter.apply(users, dto).toList();

        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(user1));
        assertFalse(filteredUsers.contains(user2));
    }
}
