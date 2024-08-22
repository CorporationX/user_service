package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.*;

class UserCityFilterTest {
    private UserCityFilter userCityFilter;
    private UserFilterDto userFilterDto;
    private User user;

    @BeforeEach
    public void setUp() {
        userCityFilter = new UserCityFilter();
        userFilterDto = new UserFilterDto();
        user = new User();
        userFilterDto.setCity("Moscow");
        user.setCity("moscow");
    }

    @Test
    public void testPositiveCheckingForNull() {
        boolean result = userCityFilter.checkingForNull(userFilterDto);
        assertTrue(result);
    }

    @Test
    public void testNegativeCheckingForNull() {
        userFilterDto.setCity(null);
        boolean result = userCityFilter.checkingForNull(userFilterDto);
        assertFalse(result);
    }

    @Test
    public void testPositiveFilterUsers() {
        boolean result = userCityFilter.filterUsers(user, userFilterDto);
        assertTrue(result);
    }

    @Test
    public void testNegativeFilterUsers() {
        userFilterDto.setCity("mMoscow");
        boolean result = userCityFilter.filterUsers(user, userFilterDto);
        assertFalse(result);
    }
}