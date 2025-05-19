package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FollowerPhonePatternFilterTest {
    private final FollowerPhonePatternFilter phonePatternFilter = new FollowerPhonePatternFilter();
    private User user;
    private UserFilterDto filter;

    @BeforeEach
    void setUp() {
        user = new User();
        filter = new UserFilterDto();
        user.setPhone("+1234567890");
    }

    @Nested
    @DisplayName("when pattern and phone are present")
    class MatchAndNonMatching {
        @Test
        @DisplayName("should match phone pattern starting with the same digits with plus")
        void shouldMatchWithPlus() {
            filter.setPhonePattern("+123");
            assertTrue(phonePatternFilter.apply(user, filter));
        }

        @Test
        @DisplayName("should match phone pattern starting with the same digits without plus")
        void shouldMatchWithoutPlus() {
            filter.setPhonePattern("123");
            assertTrue(phonePatternFilter.apply(user, filter));
        }

        @Test
        @DisplayName("should match when phone pattern contains non-digits (spaces, dashes, extra pluses")
        void shouldNormalizePattern() {
            filter.setPhonePattern("++1 23-");
            assertTrue(phonePatternFilter.apply(user, filter));
        }

        @Test
        @DisplayName("should not match when prefix differs")
        void shouldNotMatchWhenPrefixDiffers() {
            filter.setPhonePattern("2");
            assertFalse(phonePatternFilter.apply(user, filter));
        }
    }

    @Nested
    @DisplayName("when phone pattern is null or blank")
    class NullOrBlankPattern {
        @Test
        @DisplayName("empty phone pattern should return true")
        void emptyPatternShouldReturnTrue() {
            filter.setPhonePattern("");
            assertTrue(phonePatternFilter.apply(user, filter));
        }

        @Test
        @DisplayName("blank phone pattern should return true")
        void blankPatternShouldReturnTrue() {
            filter.setPhonePattern("   ");
            assertTrue(phonePatternFilter.apply(user, filter));
        }

        @Test
        @DisplayName("null phone pattern should return true")
        void nullPatternShouldReturnTrue() {
            filter.setPhonePattern(null);
            assertTrue(phonePatternFilter.apply(user, filter));
        }
    }

    @Test
    @DisplayName("should return true when phone is null and phone pattern is null")
    void nullPhoneShouldReturnTrue() {
        user.setPhone(null);
        assertTrue(phonePatternFilter.apply(user, filter));
    }

    @Test
    @DisplayName("should return false when phone is null but phone pattern is present")
    void nullPhoneShouldReturnFalseWhenPatternIsPresent() {
        user.setPhone(null);
        filter.setPhonePattern("+123");
        assertFalse(phonePatternFilter.apply(user, filter));
    }
}
