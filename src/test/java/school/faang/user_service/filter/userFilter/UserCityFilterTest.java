package school.faang.user_service.filter.userFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.ArrayList;
import java.util.List;

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
    @DisplayName("Test isApplicable with non-null about pattern")
    public void testIsApplicable_withNonNullAboutPattern() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setCityPattern("pattern");

        assertTrue(userCityFilter.isApplicable(userFilterDto));
    }

    @Test
    @DisplayName("Test isApplicable with null about pattern")
    public void testIsApplicable_withNullAboutPattern() {
        UserFilterDto userFilterDto = new UserFilterDto();

        assertFalse(userCityFilter.isApplicable(userFilterDto));
    }

    @Test
    @DisplayName("Test apply with matching pattern")
    public void testApply_WithMatchingPattern() {
        UserFilterDto userFilterDto = mock(UserFilterDto.class);
        when(userFilterDto.getCityPattern()).thenReturn(".*test.*");

        User firstUser = mock(User.class);
        when(firstUser.getCity()).thenReturn("This is a test city");

        User secondUser = mock(User.class);
        when(secondUser.getCity()).thenReturn("Another city");

        List<User> users = new ArrayList<>();
        users.add(firstUser);
        users.add(secondUser);

        List<User> filteredUsers = users.stream()
                .filter(user -> user.getCity().matches(userFilterDto.getCityPattern()))
                .toList();

        assertEquals(1, filteredUsers.size());
        assertEquals("This is a test city", filteredUsers.get(0).getCity());
        assertEquals(filteredUsers, userCityFilter.apply(users, userFilterDto).toList());
    }

    @Test
    @DisplayName("Test apply with non-matching pattern")
    public void testApply_WithNonMatchingPattern() {
        UserFilterDto userFilterDto = mock(UserFilterDto.class);
        when(userFilterDto.getCityPattern()).thenReturn(".*nonmatching.*");

        User firstUser = mock(User.class);
        when(firstUser.getCity()).thenReturn("This is a test city");

        User secondUser = mock(User.class);
        when(secondUser.getCity()).thenReturn("Another city");

        List<User> users = new ArrayList<>();
        users.add(firstUser);
        users.add(secondUser);

        List<User> filteredUsers = users.stream()
                .filter(user -> user.getCity().matches(userFilterDto.getCityPattern()))
                .toList();

        assertEquals(0, filteredUsers.size());
        assertEquals(filteredUsers, userCityFilter.apply(users, userFilterDto).toList());
    }
}
