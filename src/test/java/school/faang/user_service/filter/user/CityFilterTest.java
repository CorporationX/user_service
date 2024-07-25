package school.faang.user_service.filter.user;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

class CityFilterTest {
    private User userFirst;
    private User userSecond;
    private User userThird;
    private CityFilter cityFilter;
    private UserFilterDto userFilterDto;

    @BeforeEach
    void setup() {
        userFirst = new User();
        userSecond = new User();
        userThird = new User();
        cityFilter = new CityFilter();
        userFilterDto = mock(UserFilterDto.class);
    }

    @Test
    void testIsApplicableWhenCityPatternIsNotNull() {
        when(userFilterDto.getCityPattern()).thenReturn("Moscow");
        assertTrue(cityFilter.isApplicable(userFilterDto));
    }

    @Test
    void testIsApplicableWhenCityPatternIsNull() {
        when(userFilterDto.getCityPattern()).thenReturn(null);
        assertFalse(cityFilter.isApplicable(userFilterDto));
    }

    @Test
    void testApplyWhenUsersMatchCityPattern() {
        List<User> filteredUsers = getUserList();
        when(userFilterDto.getCityPattern()).thenReturn("Moscow");

        filteredUsers = cityFilter.apply(filteredUsers.stream(), userFilterDto).toList();
        assertEquals(2, filteredUsers.size());
        assertTrue(filteredUsers.contains(filteredUsers.get(0)));
        assertTrue(filteredUsers.contains(filteredUsers.get(1)));
    }

    @Test
    void testApplyWhenNoUsersMatchCityPattern() {
        List<User> filteredUsers = getUserList();
        when(userFilterDto.getCityPattern()).thenReturn("New York");
        filteredUsers = cityFilter.apply(filteredUsers.stream(), userFilterDto).toList();
        assertEquals(0, filteredUsers.size());
        assertTrue(filteredUsers.isEmpty());
    }

    List<User> getUserList() {
        userFirst.setCity("Moscow");
        userSecond.setCity("Moscow");
        userThird.setCity("others");
        return List.of(userFirst, userSecond, userThird);
    }
}