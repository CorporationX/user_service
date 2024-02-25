package school.faang.user_service.filter.user;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserCityFilterTest {

    @Test
    void testIsApplicableWithPattern() {
        UserCityFilter filter = new UserCityFilter();
        UserFilterDto dto = new UserFilterDto();
        dto.setCityPattern("Москва");

        assertTrue(filter.isApplicable(dto));
    }

    @Test
    void testIsApplicableWithoutPattern() {
        UserCityFilter filter = new UserCityFilter();
        UserFilterDto dto = new UserFilterDto();

        assertFalse(filter.isApplicable(dto));
    }

    @Test
    void testApplyFilter() {
        UserCityFilter filter = new UserCityFilter();
        UserFilterDto dto = new UserFilterDto();
        dto.setCityPattern("Москва");

        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        Mockito.when(user1.getCity()).thenReturn("Москва");
        Mockito.when(user2.getCity()).thenReturn("Санкт-Петербург");

        Stream<User> users = Stream.of(user1, user2);
        List<User> filteredUsers = filter.apply(users, dto).toList();

        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(user1));
        assertFalse(filteredUsers.contains(user2));
    }
}
