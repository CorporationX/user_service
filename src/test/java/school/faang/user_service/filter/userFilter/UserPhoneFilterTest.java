package school.faang.user_service.filter.userFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserPhoneFilterTest {
    private UserPhoneFilter userPhoneFilter;

    @BeforeEach
    public void setUp() {
        userPhoneFilter = new UserPhoneFilter();
    }

    @Test
    @DisplayName("Test isApplicable with non-null about pattern")
    public void testIsApplicable_withNonNullAboutPattern() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setPhonePattern("pattern");

        assertTrue(userPhoneFilter.isApplicable(userFilterDto));
    }

    @Test
    @DisplayName("Test isApplicable with null about pattern")
    public void testIsApplicable_withNullAboutPattern() {
        UserFilterDto userFilterDto = new UserFilterDto();

        assertFalse(userPhoneFilter.isApplicable(userFilterDto));
    }

    @Test
    @DisplayName("Test apply with matching pattern")
    public void testApply_WithMatchingPattern() {
        UserFilterDto userFilterDto = mock(UserFilterDto.class);
        when(userFilterDto.getPhonePattern()).thenReturn(".*test.*");

        User firstUser = mock(User.class);
        when(firstUser.getPhone()).thenReturn("This is a test phone");

        User secondUser = mock(User.class);
        when(secondUser.getPhone()).thenReturn("Another phone");

        List<User> users = new ArrayList<>();
        users.add(firstUser);
        users.add(secondUser);

        List<User> filteredUsers = users.stream()
                .filter(user -> user.getPhone().matches(userFilterDto.getPhonePattern()))
                .toList();

        assertEquals(1, filteredUsers.size());
        assertEquals("This is a test phone", filteredUsers.get(0).getPhone());
        assertEquals(filteredUsers, userPhoneFilter.apply(users, userFilterDto).toList());
    }

    @Test
    @DisplayName("Test apply with non-matching pattern")
    public void testApply_WithNonMatchingPattern() {
        UserFilterDto userFilterDto = mock(UserFilterDto.class);
        when(userFilterDto.getPhonePattern()).thenReturn(".*nonmatching.*");

        User firstUser = mock(User.class);
        when(firstUser.getPhone()).thenReturn("This is a test phone");

        User secondUser = mock(User.class);
        when(secondUser.getPhone()).thenReturn("Another phone");

        List<User> users = new ArrayList<>();
        users.add(firstUser);
        users.add(secondUser);


        List<User> filteredUsers = users.stream()
                .filter(user -> user.getPhone().matches(userFilterDto.getPhonePattern()))
                .toList();

        assertEquals(0, filteredUsers.size());
        assertEquals(filteredUsers, userPhoneFilter.apply(users, userFilterDto).toList());
    }
}
