package school.faang.user_service.filter.userFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserEmailFilterTest {
    private UserEmailFilter userEmailFilter;

    @BeforeEach
    public void setUp() {
        userEmailFilter = new UserEmailFilter();
    }

    @Test
    @DisplayName("Test isApplicable with non-null about pattern")
    public void testIsApplicable_withNonNullAboutPattern() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setEmailPattern("pattern");

        assertTrue(userEmailFilter.isApplicable(userFilterDto));
    }

    @Test
    @DisplayName("Test isApplicable with null about pattern")
    public void testIsApplicable_withNullAboutPattern() {
        UserFilterDto userFilterDto = new UserFilterDto();

        assertFalse(userEmailFilter.isApplicable(userFilterDto));
    }

    @Test
    @DisplayName("Test apply with matching pattern")
    public void testApply_WithMatchingPattern() {
        UserFilterDto userFilterDto = mock(UserFilterDto.class);
        when(userFilterDto.getEmailPattern()).thenReturn(".*test.*");

        User firstUser = mock(User.class);
        when(firstUser.getEmail()).thenReturn("This is a test email");

        User secondUser = mock(User.class);
        when(secondUser.getEmail()).thenReturn("Another email");

        List<User> users = new ArrayList<>();
        users.add(firstUser);
        users.add(secondUser);

        List<User> filteredUsers = users.stream()
                .filter(user -> user.getEmail().matches(userFilterDto.getEmailPattern()))
                .toList();

        assertEquals(1, filteredUsers.size());
        assertEquals("This is a test email", filteredUsers.get(0).getEmail());
        assertEquals(filteredUsers, userEmailFilter.apply(users, userFilterDto).toList());
    }

    @Test
    @DisplayName("Test apply with non-matching pattern")
    public void testApply_WithNonMatchingPattern() {
        UserFilterDto userFilterDto = mock(UserFilterDto.class);
        when(userFilterDto.getEmailPattern()).thenReturn(".*nonmatching.*");

        User firstUser = mock(User.class);
        when(firstUser.getEmail()).thenReturn("This is a test email");

        User secondUser = mock(User.class);
        when(secondUser.getEmail()).thenReturn("Another email");

        List<User> users = new ArrayList<>();
        users.add(firstUser);
        users.add(secondUser);

        List<User> filteredUsers = users.stream()
                .filter(user -> user.getEmail().matches(userFilterDto.getEmailPattern()))
                .toList();

        assertEquals(0, filteredUsers.size());
        assertEquals(filteredUsers, userEmailFilter.apply(users, userFilterDto).toList());
    }
}
