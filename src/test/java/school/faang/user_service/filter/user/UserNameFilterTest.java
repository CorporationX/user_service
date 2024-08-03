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
    void setUp() {
        userNameFilter = new UserNameFilter();
        userFilterDto = new UserFilterDto();
        user = new User();
        userFilterDto.setName("Alex");
        user.setUsername("alex");
    }

    @Test
    void positiveTestCheckingForNull() {
        boolean result = userNameFilter.checkingForNull(userFilterDto);
        assertTrue(result);
    }

    @Test
    void negativeTestCheckingForNull() {
        userFilterDto.setName(null);
        boolean result = userNameFilter.checkingForNull(userFilterDto);
        assertFalse(result);
    }

    @Test
    void positiveTestFilterUsers() {
        boolean result = userNameFilter.filterUsers(user, userFilterDto);
        assertTrue(result);
    }

    @Test
    void negativeTestFilterUsers() {
        userFilterDto.setName("Alexander");
        boolean result = userNameFilter.filterUsers(user, userFilterDto);
        assertFalse(result);
    }
}