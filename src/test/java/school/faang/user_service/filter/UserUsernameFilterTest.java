package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.*;

class UserUsernameFilterTest {

    public static final String NAME_REGEX = "^[A-Z].*";

    private UserUsernameFilter userUsernameFilter;
    private UserFilterDto filters;
    private User user;

    @BeforeEach
    void setUp() {
        userUsernameFilter = new UserUsernameFilter();
        filters = new UserFilterDto();
        user = new User();
    }

    @Test
    void isApplicable_ShouldReturnTrue_WhenFilterHasNamePattern() {
        filters.setNamePattern(NAME_REGEX);

        boolean result = userUsernameFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenFilterIsNull() {
        boolean result = userUsernameFilter.isApplicable(null);

        assertFalse(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenNamePatternIsNull() {
        filters.setNamePattern(null);

        boolean result = userUsernameFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void apply_ShouldReturnTrue_WhenUserUsernameMatchesPattern() {
        filters.setNamePattern(NAME_REGEX);
        user.setUsername("Alice");

        boolean result = userUsernameFilter.apply(user, filters);

        assertTrue(result);
    }

    @Test
    void apply_ShouldReturnFalse_WhenUserUsernameDoesNotMatchPattern() {
        filters.setNamePattern(NAME_REGEX);
        user.setUsername("alice");

        boolean result = userUsernameFilter.apply(user, filters);

        assertFalse(result);
    }

    @Test
    void apply_ShouldReturnFalse_WhenUserIsNull() {
        boolean result = userUsernameFilter.apply(null, filters);

        assertFalse(result);
    }

    @Test
    void apply_ShouldReturnFalse_WhenUserUsernameIsNull() {
        user.setUsername(null);
        boolean result = userUsernameFilter.apply(user, filters);

        assertFalse(result);
    }
}