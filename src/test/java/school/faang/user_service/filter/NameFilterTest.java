package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.filter.NameFilter;
import school.faang.user_service.entity.User;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NameFilterTest {

    private NameFilter nameFilter;

    @BeforeEach
    public void setUp() {
        nameFilter = new NameFilter();
    }

    @Test
    public void testIsApplicableWithNullNamePattern() {
        UserFilterDto filter = mock(UserFilterDto.class);
        when(filter.getNamePattern()).thenReturn(null);

        boolean result = nameFilter.isApplicable(filter);

        assertFalse(result);
    }

    @Test
    public void testIsApplicableWithNamePattern() {
        UserFilterDto filter = mock(UserFilterDto.class);
        when(filter.getNamePattern()).thenReturn("test");

        boolean result = nameFilter.isApplicable(filter);

        assertTrue(result);
    }

    @Test
    public void testApplyWithMatchingUsernames() {
        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);

        UserFilterDto filter = mock(UserFilterDto.class);
        when(filter.getNamePattern()).thenReturn("test");
        when(user1.getUsername()).thenReturn("testUser1");
        when(user2.getUsername()).thenReturn("exampleUser2");

        List<User> userList = Arrays.asList(user1, user2);
        Stream<User> userStream = userList.stream();

        Stream<User> resultStream = nameFilter.apply(userStream, filter);
        List<User> resultList = resultStream.toList();

        assertEquals(1, resultList.size());
        assertEquals("testUser1", resultList.get(0).getUsername());
    }

    @Test
    public void testApplyWithNoMatchingUsernames() {
        User user1 = new User();
        user1.setUsername("exampleUser1");
        User user2 = new User();
        user2.setUsername("exampleUser2");

        UserFilterDto filter = mock(UserFilterDto.class);
        when(filter.getNamePattern()).thenReturn("test");

        List<User> userList = Arrays.asList(user1, user2);
        Stream<User> userStream = userList.stream();

        Stream<User> resultStream = nameFilter.apply(userStream, filter);
        List<User> resultList = resultStream.toList();

        assertEquals(0, resultList.size());
    }
}
