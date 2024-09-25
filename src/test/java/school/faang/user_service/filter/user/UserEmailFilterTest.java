package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserEmailFilterTest {

    public static final String EMAIL_REGEX = "^\\S+@\\S+\\.\\S+$";

    private UserEmailFilter userEmailFilter;
    private UserFilterDto filters;
    private User user;

    @BeforeEach
    void setUp() {
        userEmailFilter = new UserEmailFilter();
        filters = new UserFilterDto();
        user = new User();
    }

    @Test
    void isApplicable_ShouldReturnTrue_WhenFilterHasEmailPattern() {
        filters.setEmailPattern("^\\S+@\\S+\\.\\S+$");

        boolean result = userEmailFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenFilterIsNull() {
        boolean result = userEmailFilter.isApplicable(null);

        assertFalse(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenEmailPatternIsNull() {
        filters.setEmailPattern(null);

        boolean result = userEmailFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void apply_ShouldReturnTrue_WhenUserEmailMatchesPattern() {
        filters.setEmailPattern(EMAIL_REGEX);
        user.setEmail("test@example.com");

        boolean result = userEmailFilter.apply(user, filters);

        assertTrue(result);
    }

    @Test
    void apply_ShouldReturnFalse_WhenUserEmailDoesNotMatchPattern() {
        filters.setEmailPattern(EMAIL_REGEX);
        user.setEmail("invalid-email");

        boolean result = userEmailFilter.apply(user, filters);

        assertFalse(result);
    }

    @Test
    void apply_ShouldReturnFalse_WhenUserIsNull() {
        boolean result = userEmailFilter.apply(null, filters);

        assertFalse(result);
    }

    @Test
    void apply_ShouldReturnFalse_WhenUserEmailIsNull() {
        user.setEmail(null);
        boolean result = userEmailFilter.apply(user, filters);

        assertFalse(result);
    }
}
