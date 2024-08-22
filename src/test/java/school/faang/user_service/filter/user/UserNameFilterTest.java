package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.*;

class UserNameFilterTest {
    private UserNameFilter userNameFilter;
    private UserFilterDto userFilterDto;
    private User user;

    @BeforeEach
    public void setUp() {
        userNameFilter = new UserNameFilter();
        userFilterDto = new UserFilterDto();
        user = new User();
        userFilterDto.setName("Alex");
        user.setUsername("alex");
    }

    @Test
    public void testPositiveCheckingForNull() {
        boolean result = userNameFilter.checkingForNull(userFilterDto);
        assertTrue(result);
    }

    @Test
    public void testNegativeCheckingForNull() {
        userFilterDto.setName(null);
        boolean result = userNameFilter.checkingForNull(userFilterDto);
        assertFalse(result);
    }

    @Test
    public void testPositiveFilterUsers() {
        boolean result = userNameFilter.filterUsers(user, userFilterDto);
        assertTrue(result);
    }

    @Test
    public void testNegativeFilterUsers() {
        userFilterDto.setName("Alexander");
        boolean result = userNameFilter.filterUsers(user, userFilterDto);
        assertFalse(result);
    }
}