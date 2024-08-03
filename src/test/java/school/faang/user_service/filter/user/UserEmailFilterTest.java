package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.*;

class UserEmailFilterTest {
    private UserEmailFilter userEmailFilter;
    private UserFilterDto userFilterDto;
    private User user;

    @BeforeEach
    void setUp() {
        userEmailFilter = new UserEmailFilter();
        userFilterDto = new UserFilterDto();
        user = new User();
        userFilterDto.setEmail("Alex@mail.com");
        user.setEmail("alex@mail.com");
    }

    @Test
    void positiveTestCheckingForNull() {
        boolean result = userEmailFilter.checkingForNull(userFilterDto);
        assertTrue(result);
    }

    @Test
    void negativeTestCheckingForNull() {
        userFilterDto.setEmail(null);
        boolean result = userEmailFilter.checkingForNull(userFilterDto);
        assertFalse(result);
    }

    @Test
    void positiveTestFilterUsers() {
        boolean result = userEmailFilter.filterUsers(user, userFilterDto);
        assertTrue(result);
    }

    @Test
    void negativeTestFilterUsers() {
        userFilterDto.setEmail("Alexander@mail.com");
        boolean result = userEmailFilter.filterUsers(user, userFilterDto);
        assertFalse(result);
    }
}