package school.faang.user_service.service.user.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserExtendedFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.UserFilter;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserCountryFilterTest {
    private UserFilter userCountryFilter;
    private String countryTest;
    private Country country;

    @BeforeEach
    void setUp() {
        userCountryFilter = new UserCountryFilter();
        countryTest = "Russia";
        country = new Country();
        country.setTitle(countryTest);
    }

    @Test
    void testIsApplicable_patternWithFilledCountry() {
        UserExtendedFilterDto userFilterDto = new UserExtendedFilterDto();
        userFilterDto.setCountryPattern(countryTest);
        boolean isApplicable = userCountryFilter.isApplicable(userFilterDto);

        assertTrue(isApplicable);
    }

    @Test
    void testIsApplicable_patternWithNotFilledCountry() {
        UserExtendedFilterDto userFilterDto = new UserExtendedFilterDto();
        boolean isApplicable = userCountryFilter.isApplicable(userFilterDto);

        assertFalse(isApplicable);
    }

    @Test
    void testGetPredicate_successCountryValidation() {
        UserExtendedFilterDto userFilterDto = new UserExtendedFilterDto();
        userFilterDto.setCountryPattern(countryTest);

        User user = new User();
        user.setCountry(country);

        Predicate<User> predicate =  userCountryFilter.getPredicate(userFilterDto);

        boolean result = predicate.test(user);

        assertTrue(result);
    }

    @Test
    void testGetPredicate_failedCountryValidation() {
        Country country1 = new Country();
        country1.setTitle("America");

        UserExtendedFilterDto userFilterDto = new UserExtendedFilterDto();
        userFilterDto.setCountryPattern(countryTest);

        User user = new User();
        user.setCountry(country1);

        Predicate<User> predicate =  userCountryFilter.getPredicate(userFilterDto);

        boolean result = predicate.test(user);

        assertFalse(result);
    }
}
