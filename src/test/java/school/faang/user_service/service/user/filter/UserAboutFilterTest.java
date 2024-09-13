package school.faang.user_service.service.user.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.UserFilter;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserAboutFilterTest {
    private UserFilter userAboutFilter;
    private String aboutTest;

    @BeforeEach
    void setUp() {
        userAboutFilter = new UserAboutFilter();
        aboutTest = "Developer";
    }

    @Test
    void testIsApplicable_patternWithFilledAbout() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setAboutPattern(aboutTest);
        boolean isApplicable = userAboutFilter.isApplicable(userFilterDto);

        assertTrue(isApplicable);
    }

    @Test
    void testIsApplicable_patternWithNotFilledAbout() {
        UserFilterDto userFilterDto = new UserFilterDto();
        boolean isApplicable = userAboutFilter.isApplicable(userFilterDto);

        assertFalse(isApplicable);
    }

    @Test
    void testGetPredicate_successValidation() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setAboutPattern(aboutTest);

        User user = new User();
        user.setAboutMe(aboutTest);

        Predicate<User> predicate =  userAboutFilter.getPredicate(userFilterDto);

        boolean result = predicate.test(user);

        assertTrue(result);
    }

    @Test
    void testGetPredicate_failedValidation() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setAboutPattern(aboutTest);

        User user = new User();
        user.setAboutMe("Manager");

        Predicate<User> predicate =  userAboutFilter.getPredicate(userFilterDto);

        boolean result = predicate.test(user);

        assertFalse(result);
    }
}
