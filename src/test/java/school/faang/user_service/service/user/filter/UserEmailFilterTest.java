package school.faang.user_service.service.user.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserExtendedFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.UserFilter;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserEmailFilterTest {
    private UserFilter userEmailFilter;
    private String emailTest;

    @BeforeEach
    void setUp() {
        userEmailFilter = new EmailFilter();
        emailTest = "test@test.com";
    }

    @Test
    void testIsApplicable_patternWithFilledEmail() {
        UserExtendedFilterDto userFilterDto = new UserExtendedFilterDto();
        userFilterDto.setEmailPattern(emailTest);
        boolean isApplicable = userEmailFilter.isApplicable(userFilterDto);

        assertTrue(isApplicable);
    }

    @Test
    void testIsApplicable_patternWithNotFilledEmail() {
        UserExtendedFilterDto userFilterDto = new UserExtendedFilterDto();
        boolean isApplicable = userEmailFilter.isApplicable(userFilterDto);

        assertFalse(isApplicable);
    }

    @Test
    void testGetPredicate_successValidation() {
        UserExtendedFilterDto userFilterDto = new UserExtendedFilterDto();
        userFilterDto.setEmailPattern(emailTest);

        User user = new User();
        user.setEmail(emailTest);

        Predicate<User> predicate =  userEmailFilter.getPredicate(userFilterDto);

        boolean result = predicate.test(user);

        assertTrue(result);
    }

    @Test
    void testGetPredicate_failedValidation() {
        UserExtendedFilterDto userFilterDto = new UserExtendedFilterDto();
        userFilterDto.setEmailPattern(emailTest);

        User user = new User();
        user.setEmail("test1@test.com");

        Predicate<User> predicate =  userEmailFilter.getPredicate(userFilterDto);

        boolean result = predicate.test(user);

        assertFalse(result);
    }
}
