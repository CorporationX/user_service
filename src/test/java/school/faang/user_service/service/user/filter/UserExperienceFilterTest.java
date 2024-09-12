package school.faang.user_service.service.user.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.UserFilter;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserExperienceFilterTest {

    private UserFilter userExperienceFilter;
    private int userExperience;
    private int experienceMin;
    private int experienceMax;

    @BeforeEach
    void setUp() {
        userExperienceFilter = new UserExperienceFilter();
        userExperience = 4;
        experienceMin = 2;
        experienceMax = 7;
    }

    @Test
    void testIsApplicable_patternWithFilledExperienceMin() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setExperienceMin(experienceMin);
        boolean isApplicable = userExperienceFilter.isApplicable(userFilterDto);

        assertTrue(isApplicable);
    }

    @Test
    void testIsApplicable_patternWithFilledExperienceMax() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setExperienceMin(experienceMax);
        boolean isApplicable = userExperienceFilter.isApplicable(userFilterDto);

        assertTrue(isApplicable);
    }

    @Test
    void testIsApplicable_patternWithFilledExperienceMinMax() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setExperienceMin(experienceMin);
        userFilterDto.setExperienceMin(experienceMax);
        boolean isApplicable = userExperienceFilter.isApplicable(userFilterDto);

        assertTrue(isApplicable);
    }

    @Test
    void testIsApplicable_patternWithNotFilledExperience() {
        UserFilterDto userFilterDto = new UserFilterDto();
        boolean isApplicable = userExperienceFilter.isApplicable(userFilterDto);

        assertFalse(isApplicable);
    }

    @Test
    void testGetPredicate_successValidationWithFilledExperienceMin() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setExperienceMin(experienceMin);

        User user = new User();
        user.setExperience(userExperience);

        Predicate<User> predicate =  userExperienceFilter.getPredicate(userFilterDto);

        boolean result = predicate.test(user);

        assertTrue(result);
    }

    @Test
    void testGetPredicate_successValidationWithFilledExperienceMax() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setExperienceMax(experienceMax);

        User user = new User();
        user.setExperience(userExperience);

        Predicate<User> predicate =  userExperienceFilter.getPredicate(userFilterDto);

        boolean result = predicate.test(user);

        assertTrue(result);
    }

    @Test
    void testGetPredicate_successValidationWithFilledExperienceMinMax() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setExperienceMin(experienceMin);
        userFilterDto.setExperienceMax(experienceMax);

        User user = new User();
        user.setExperience(userExperience);

        Predicate<User> predicate =  userExperienceFilter.getPredicate(userFilterDto);

        boolean result = predicate.test(user);

        assertTrue(result);
    }

    @Test
    void testGetPredicate_failedValidationValueLowerThanMin() {
        int customMin = 5;

        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setExperienceMin(customMin);

        User user = new User();
        user.setExperience(userExperience);

        Predicate<User> predicate =  userExperienceFilter.getPredicate(userFilterDto);

        boolean result = predicate.test(user);

        assertFalse(result);
    }

    @Test
    void testGetPredicate_failedValidationValueGreaterThanMax() {
        int customMax = 3;

        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setExperienceMax(customMax);

        User user = new User();
        user.setExperience(userExperience);

        Predicate<User> predicate =  userExperienceFilter.getPredicate(userFilterDto);

        boolean result = predicate.test(user);

        assertFalse(result);
    }


}
