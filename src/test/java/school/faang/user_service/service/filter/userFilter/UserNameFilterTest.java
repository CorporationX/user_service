package school.faang.user_service.service.filter.userFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.userFilter.UserNameFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserNameFilterTest {
    private UserNameFilter userNameFilter;

    @BeforeEach
    public void setUp() {
        userNameFilter = new UserNameFilter();
    }

    @Test
    public void testIsApplicable_withNonNullAboutPattern() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setNamePattern("pattern");

        assertTrue(userNameFilter.isApplicable(userFilterDto));
    }

    @Test
    public void testIsApplicable_withNullAboutPattern() {
        UserFilterDto userFilterDto = new UserFilterDto();

        assertFalse(userNameFilter.isApplicable(userFilterDto));
    }

    @Test
    public void testApply_WithMatchingPattern() {
        UserFilterDto userFilterDto = mock(UserFilterDto.class);
        when(userFilterDto.getNamePattern()).thenReturn(".*test.*");

        User firstUser = mock(User.class);
        when(firstUser.getUsername()).thenReturn("This is a test name");

        User secondUser = mock(User.class);
        when(secondUser.getUsername()).thenReturn("Another name");

        List<User> users = Stream.of(firstUser, secondUser).toList();
        Stream<User> userStream = users.stream();

        userNameFilter.apply(userStream, userFilterDto);

        List<User> filteredUsers = users.stream()
                .filter(user -> user.getUsername().matches(userFilterDto.getNamePattern()))
                .toList();

        assertEquals(1, filteredUsers.size());
        assertEquals("This is a test name", filteredUsers.get(0).getUsername());
    }

    @Test
    public void testApply_WithNonMatchingPattern() {
        UserFilterDto userFilterDto = mock(UserFilterDto.class);
        when(userFilterDto.getNamePattern()).thenReturn(".*nonmatching.*");

        User firstUser = mock(User.class);
        when(firstUser.getUsername()).thenReturn("This is a test name");

        User secondUser = mock(User.class);
        when(secondUser.getUsername()).thenReturn("Another name");

        List<User> users = Stream.of(firstUser, secondUser).toList();
        Stream<User> userStream = users.stream();

        userNameFilter.apply(userStream, userFilterDto);

        List<User> filteredUsers = users.stream()
                .filter(user -> user.getUsername().matches(userFilterDto.getNamePattern()))
                .toList();

        assertEquals(0, filteredUsers.size());
    }
}
