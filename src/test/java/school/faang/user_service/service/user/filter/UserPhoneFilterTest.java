package school.faang.user_service.service.user.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.UserFilter;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserPhoneFilterTest {

    private UserFilter userPhoneFilter;
    private String phoneTest;

    @BeforeEach
    void setUp() {
        userPhoneFilter = new UserPhoneFilter();
        phoneTest = "777";
    }

    @Test
    void testIsApplicable_patternWithFilledPhone() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setPhonePattern(phoneTest);
        boolean isApplicable = userPhoneFilter.isApplicable(userFilterDto);

        assertTrue(isApplicable);
    }

    @Test
    void testIsApplicable_patternWithNotFilledPhone() {
        UserFilterDto userFilterDto = new UserFilterDto();
        boolean isApplicable = userPhoneFilter.isApplicable(userFilterDto);

        assertFalse(isApplicable);
    }

    @Test
    void testGetPredicate_successValidation() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setPhonePattern(phoneTest);

        User user = new User();
        user.setPhone(phoneTest);

        Predicate<User> predicate =  userPhoneFilter.getPredicate(userFilterDto);

        boolean result = predicate.test(user);

        assertTrue(result);
    }

    @Test
    void testGetPredicate_failedValidation() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setPhonePattern(phoneTest);

        User user = new User();
        user.setPhone("2222");

        Predicate<User> predicate =  userPhoneFilter.getPredicate(userFilterDto);

        boolean result = predicate.test(user);

        assertFalse(result);
    }


}
