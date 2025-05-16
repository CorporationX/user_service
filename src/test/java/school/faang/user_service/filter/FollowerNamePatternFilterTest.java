package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class FollowerNamePatternFilterTest {
    private final FollowerNamePatternFilter namePatternFilter = new FollowerNamePatternFilter();
    private User user;
    private UserFilterDto filter;

    @Nested
    @DisplayName("Prefix, word boundaries and case insensitivity")
    class BasicTests {
        @BeforeEach
        void setUp() {
            user = new User();
            filter = new UserFilterDto();
            user.setUsername("Anatoly");
        }

        @Test
        @DisplayName("should match exact word ignoring case")
        void shouldMatchExactWordIgnoringCase() {
            filter.setNamePattern("AnAToLy");
            assertTrue(namePatternFilter.apply(user, filter));
        }

        @Test
        @DisplayName("should not match")
        void shouldNotMatch() {
            filter.setNamePattern("Anna");
            assertFalse(namePatternFilter.apply(user, filter));
        }
    }

    @Nested
    @DisplayName("Diacritic normalization and edge cases")
    class DiacriticAndEdgeCases {
        @BeforeEach
        void setUp() {
            user = new User();
            filter = new UserFilterDto();
            user.setUsername("Émile");
        }

        @Test
        @DisplayName("should normalize É to E")
        void shouldNormalizeDiacritic() {
            filter.setNamePattern("Emile");
            assertTrue(namePatternFilter.apply(user, filter));
        }

        @Test
        @DisplayName("empty pattern should always return true")
        void emptyPatternShouldAlwaysReturnTrue() {
            filter.setNamePattern("");
            assertTrue(namePatternFilter.apply(user, filter));
        }

        @Test
        @DisplayName("blank pattern should always return true")
        void blankPatternShouldAlwaysReturnTrue() {
            filter.setNamePattern("    ");
            assertTrue(namePatternFilter.apply(user, filter));
        }

        @Test
        @DisplayName("null pattern should always return true ")
        void shouldReturnTrueWhenFilterNamePatternIsNull() {
            filter.setNamePattern(null);
            assertTrue(namePatternFilter.apply(user, filter));
        }
    }
}
