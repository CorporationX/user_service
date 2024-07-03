package school.faang.user_service.service.filter.userFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.userFilter.UserPhoneFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserPhoneFilterTest {
    private UserPhoneFilter userPhoneFilter;

    @BeforeEach
    public void setUp() {
        userPhoneFilter = new UserPhoneFilter();
    }

    @Test
    public void testIsApplicable_withNonNullAboutPattern() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setPhonePattern("pattern");

        assertTrue(userPhoneFilter.isApplicable(userFilterDto));
    }

    @Test
    public void testIsApplicable_withNullAboutPattern() {
        UserFilterDto userFilterDto = new UserFilterDto();

        assertFalse(userPhoneFilter.isApplicable(userFilterDto));
    }

    @Test
    public void testApply_WithMatchingPattern() {
        UserFilterDto userFilterDto = mock(UserFilterDto.class);
        when(userFilterDto.getPhonePattern()).thenReturn(".*test.*");

        User firstUser = mock(User.class);
        when(firstUser.getPhone()).thenReturn("This is a test phone");

        User secondUser = mock(User.class);
        when(secondUser.getPhone()).thenReturn("Another phone");

        List<User> users = Stream.of(firstUser, secondUser).toList();
        Stream<User> userStream = users.stream();

        userPhoneFilter.apply(userStream, userFilterDto);

        List<User> filteredUsers = users.stream()
                .filter(user -> user.getPhone().matches(userFilterDto.getPhonePattern()))
                .toList();

        assertEquals(1, filteredUsers.size());
        assertEquals("This is a test phone", filteredUsers.get(0).getPhone());
    }

    @Test
    public void testApply_WithNonMatchingPattern() {
        UserFilterDto userFilterDto = mock(UserFilterDto.class);
        when(userFilterDto.getPhonePattern()).thenReturn(".*nonmatching.*");

        User firstUser = mock(User.class);
        when(firstUser.getPhone()).thenReturn("This is a test phone");

        User secondUser = mock(User.class);
        when(secondUser.getPhone()).thenReturn("Another phone");

        List<User> users = Stream.of(firstUser, secondUser).toList();
        Stream<User> userStream = users.stream();

        userPhoneFilter.apply(userStream, userFilterDto);

        List<User> filteredUsers = users.stream()
                .filter(user -> user.getPhone().matches(userFilterDto.getPhonePattern()))
                .toList();

        assertEquals(0, filteredUsers.size());
    }
}
