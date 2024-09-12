package school.faang.user_service.service.user.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.UserFilter;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserCityFilterTest {

    private UserFilter userCityFilter;
    private String cityTest;

    @BeforeEach
    void setUp() {
        userCityFilter = new UserCityFilter();
        cityTest = "Moscow";
    }

    @Test
    void testIsApplicable_patternWithFilledCity() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setCityPattern(cityTest);
        boolean isApplicable = userCityFilter.isApplicable(userFilterDto);

        assertTrue(isApplicable);
    }

    @Test
    void testIsApplicable_patternWithNotFilledCity() {
        UserFilterDto userFilterDto = new UserFilterDto();
        boolean isApplicable = userCityFilter.isApplicable(userFilterDto);

        assertFalse(isApplicable);
    }

    @Test
    void testGetPredicate_successCityValidation() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setCityPattern(cityTest);

        User user = new User();
        user.setCity(cityTest);

        Predicate<User> predicate =  userCityFilter.getPredicate(userFilterDto);

        boolean result = predicate.test(user);

        assertTrue(result);
    }

    @Test
    void testGetPredicate_failedCityValidation() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setCityPattern(cityTest);

        User user = new User();
        user.setCity("Izhevsk");

        Predicate<User> predicate =  userCityFilter.getPredicate(userFilterDto);

        boolean result = predicate.test(user);

        assertFalse(result);
    }


}
