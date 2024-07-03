package school.faang.user_service.service.filter.userFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.userFilter.UserCityFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserCityFilterTest {
    private UserCityFilter userCityFilter;

    @BeforeEach
    public void setUp() {
        userCityFilter = new UserCityFilter();
    }

    @Test
    public void testIsApplicable_withNonNullAboutPattern() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setCityPattern("pattern");

        assertTrue(userCityFilter.isApplicable(userFilterDto));
    }

    @Test
    public void testIsApplicable_withNullAboutPattern() {
        UserFilterDto userFilterDto = new UserFilterDto();

        assertFalse(userCityFilter.isApplicable(userFilterDto));
    }

    @Test
    public void testApply_WithMatchingPattern() {
        UserFilterDto userFilterDto = mock(UserFilterDto.class);
        when(userFilterDto.getCityPattern()).thenReturn(".*test.*");

        User firstUser = mock(User.class);
        when(firstUser.getCity()).thenReturn("This is a test city");

        User secondUser = mock(User.class);
        when(secondUser.getCity()).thenReturn("Another city");

        List<User> users = Stream.of(firstUser, secondUser).toList();
        Stream<User> userStream = users.stream();

        userCityFilter.apply(userStream, userFilterDto);

        List<User> filteredUsers = users.stream()
                .filter(user -> user.getCity().matches(userFilterDto.getCityPattern()))
                .toList();

        assertEquals(1, filteredUsers.size());
        assertEquals("This is a test city", filteredUsers.get(0).getCity());
    }

    @Test
    public void testApply_WithNonMatchingPattern() {
        UserFilterDto userFilterDto = mock(UserFilterDto.class);
        when(userFilterDto.getCityPattern()).thenReturn(".*nonmatching.*");

        User firstUser = mock(User.class);
        when(firstUser.getCity()).thenReturn("This is a test city");

        User secondUser = mock(User.class);
        when(secondUser.getCity()).thenReturn("Another city");

        List<User> users = Stream.of(firstUser, secondUser).toList();
        Stream<User> userStream = users.stream();

        userCityFilter.apply(userStream, userFilterDto);

        List<User> filteredUsers = users.stream()
                .filter(user -> user.getCity().matches(userFilterDto.getCityPattern()))
                .toList();

        assertEquals(0, filteredUsers.size());
    }
}
