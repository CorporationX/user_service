package school.faang.user_service.filter.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

import java.util.List;

class CountryFilterTest {
    private User userFirst;
    private User userSecond;
    private User userThird;
    private Country countryFirst;
    private Country countrySecond;
    private CountryFilter countryFilter;
    private UserFilterDto userFilterDto;

    @BeforeEach
    void setup() {
        countryFirst = new Country();
        countrySecond = new Country();
        userFirst = new User();
        userSecond = new User();
        userThird = new User();
        countryFilter = new CountryFilter();
        userFilterDto = mock(UserFilterDto.class);
    }

    @Test
    void testIsApplicableWhenCountryPatternIsNotNull() {
        when(userFilterDto.getCountryPattern()).thenReturn("Russia");
        assertTrue(countryFilter.isApplicable(userFilterDto));
    }

    @Test
    void testIsApplicableWhenCountryPatternIsNull() {
        when(userFilterDto.getCountryPattern()).thenReturn(null);
        assertFalse(countryFilter.isApplicable(userFilterDto));
    }

    @Test
    void testApplyWhenUsersMatchCountryPattern() {
        List<User> filteredUsers = getUserList();
        when(userFilterDto.getCountryPattern()).thenReturn("Russia");

        filteredUsers = countryFilter.apply(filteredUsers.stream(), userFilterDto).toList();
        assertEquals(2, filteredUsers.size());
        assertTrue(filteredUsers.contains(filteredUsers.get(0)));
        assertTrue(filteredUsers.contains(filteredUsers.get(1)));
    }

    @Test
    void testApplyWhenNoUsersMatchCountryPattern() {
        List<User> filteredUsers = getUserList();
        when(userFilterDto.getCountryPattern()).thenReturn("England");

        filteredUsers = countryFilter.apply(filteredUsers.stream(), userFilterDto).toList();
        assertEquals(0, filteredUsers.size());
        assertTrue(filteredUsers.isEmpty());
    }

    List<User> getUserList() {
        countryFirst.setTitle("Russia");
        countrySecond.setTitle("USA");
        userFirst.setCountry(countryFirst);
        userSecond.setCountry(countryFirst);
        userThird.setCountry(countrySecond);
        return List.of(userFirst, userSecond, userThird);
    }
}