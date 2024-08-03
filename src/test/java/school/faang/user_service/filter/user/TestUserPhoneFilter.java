package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.*;

class TestUserPhoneFilter {
    private UserPhoneFilter userPhoneFilter;
    private UserFilterDto userFilterDto;
    private User user;

    @BeforeEach
    void setUp() {
        userPhoneFilter = new UserPhoneFilter();
        userFilterDto = new UserFilterDto();
        user = new User();
        userFilterDto.setPhone("8-900-123-34-34");
        user.setPhone("89001233434");
    }

    @Test
    void positiveTestCheckingForNull() {
        boolean result = userPhoneFilter.checkingForNull(userFilterDto);
        assertTrue(result);
    }

    @Test
    void negativeTestCheckingForNull() {
        userFilterDto.setPhone(null);
        boolean result = userPhoneFilter.checkingForNull(userFilterDto);
        assertFalse(result);
    }
    @Test
    void positiveTestFilterUsers() {
        boolean result = userPhoneFilter.filterUsers(user, userFilterDto);
        assertTrue(result);

    }

    @Test
    void negativeTestFilterUsers() {
        userFilterDto.setPhone("1234567890");
        boolean result = userPhoneFilter.filterUsers(user, userFilterDto);
        assertFalse(result);
    }
}