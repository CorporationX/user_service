package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.filter.EmailFilter;
import school.faang.user_service.entity.User;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EmailFilterTest {

    private EmailFilter emailFilter;

    @BeforeEach
    public void setUp() {
        emailFilter = new EmailFilter();
    }

    @Test
    public void testIsApplicable_WithNullEmailPattern() {
        UserFilterDto filter = mock(UserFilterDto.class);
        when(filter.getEmailPattern()).thenReturn(null);

        boolean result = emailFilter.isApplicable(filter);

        assertFalse(result);
    }

    @Test
    public void testIsApplicable_WithEmailPattern() {
        UserFilterDto filter = mock(UserFilterDto.class);
        when(filter.getEmailPattern()).thenReturn("test@example.com");

        boolean result = emailFilter.isApplicable(filter);

        assertTrue(result);
    }

    @Test
    public void testApply_WithMatchingEmails() {
        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);

        UserFilterDto filter = mock(UserFilterDto.class);
        when(filter.getEmailPattern()).thenReturn("example.com");
        when(user1.getEmail()).thenReturn("user1@example.com");
        when(user2.getEmail()).thenReturn("user2@test.com");

        List<User> userList = Arrays.asList(user1, user2);
        Stream<User> userStream = userList.stream();

        Stream<User> resultStream = emailFilter.apply(userStream, filter);
        List<User> resultList = resultStream.toList();

        assertEquals(1, resultList.size());
        assertEquals("user1@example.com", resultList.get(0).getEmail());
    }

    @Test
    public void testApply_WithNoMatchingEmails() {
        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);

        UserFilterDto filter = mock(UserFilterDto.class);
        when(filter.getEmailPattern()).thenReturn("test@example.com");
        when(user1.getEmail()).thenReturn("user1@example.com");
        when(user2.getEmail()).thenReturn("user2@test.com");

        List<User> userList = Arrays.asList(user1, user2);
        Stream<User> userStream = userList.stream();

        Stream<User> resultStream = emailFilter.apply(userStream, filter);
        List<User> resultList = resultStream.toList();

        assertEquals(0, resultList.size());
    }

}
