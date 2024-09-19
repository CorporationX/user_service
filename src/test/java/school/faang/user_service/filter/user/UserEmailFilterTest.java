package school.faang.user_service.filter.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserEmailFilterTest {

    private final static String EMAIL_PATTERN = "email";

    @InjectMocks
    private UserEmailFilter userEmailFilter;

    private UserFilterDto userFilterDto;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("If UserFilterDto emailPattern not null and not blank than return true")
        void whenUserFilterDtoSpecifiedEmailPatternNotNullAndNotBlankThenReturnTrue() {
            userFilterDto = UserFilterDto.builder()
                    .emailPattern(EMAIL_PATTERN)
                    .build();

            assertTrue(userEmailFilter.isApplicable(userFilterDto));
        }

        @Test
        @DisplayName("If UserFilterDto emailPattern not null and not blank than return sorted list")
        void whenUserFilterDtoSpecifiedEmailPatternThenReturnFilteredList() {
            Stream<User> userStream = Stream.of(
                    User.builder()
                            .email(EMAIL_PATTERN)
                            .build(),
                    User.builder()
                            .email("false")
                            .build());

            userFilterDto = UserFilterDto.builder()
                    .emailPattern(EMAIL_PATTERN)
                    .build();

            Stream<User> userStreamAfterFilter = Stream.of(
                    User.builder()
                            .email(EMAIL_PATTERN)
                            .build());
            assertEquals(userStreamAfterFilter.toList(), userEmailFilter.apply(userStream, userFilterDto).toList());
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("If UserFilterDto emailPattern is blank than return false")
        void whenUserFilterDtoEmailPatternIsBlankThenReturnFalse() {
            userFilterDto = UserFilterDto.builder()
                    .emailPattern("   ")
                    .build();

            assertFalse(userEmailFilter.isApplicable(userFilterDto));
        }

        @Test
        @DisplayName("If UserFilterDto emailPattern is null than return false")
        void whenUserFilterDtoEmailPatternIsNullThenReturnFalse() {
            userFilterDto = UserFilterDto.builder()
                    .emailPattern(null)
                    .build();

            assertFalse(userEmailFilter.isApplicable(userFilterDto));
        }
    }
}