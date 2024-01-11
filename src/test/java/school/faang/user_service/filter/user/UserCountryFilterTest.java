package school.faang.user_service.filter.user;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserCountryFilterTest {

    @Test
    void testIsApplicableWithPattern() {
        UserCountryFilter filter = new UserCountryFilter();
        UserFilterDto dto = new UserFilterDto();
        dto.setCountryPattern("Россия");

        assertTrue(filter.isApplicable(dto));
    }

    @Test
    void testIsApplicableWithoutPattern() {
        UserCountryFilter filter = new UserCountryFilter();
        UserFilterDto dto = new UserFilterDto();

        assertFalse(filter.isApplicable(dto));
    }

    @Test
    void testApplyFilter() {
        UserCountryFilter filter = new UserCountryFilter();
        UserFilterDto dto = new UserFilterDto();
        dto.setCountryPattern("Россия");

        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        Country country1 = new Country();
        Country country2 = new Country();
        country1.setTitle("Россия");
        country2.setTitle("кперееуке");
        Mockito.when(user1.getCountry()).thenReturn(country1);
        Mockito.when(user2.getCountry()).thenReturn(country2);

        Stream<User> users = Stream.of(user1, user2);
        List<User> filteredUsers = filter.apply(users, dto).toList();

        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(user1));
        assertFalse(filteredUsers.contains(user2));
    }
}
