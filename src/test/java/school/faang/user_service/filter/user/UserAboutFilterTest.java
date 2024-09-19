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
class UserAboutFilterTest {

    private final static String ABOUT_PATTERN = "about";

    @InjectMocks
    private UserAboutFilter userAboutFilter;

    private UserFilterDto userFilterDto;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("If UserFilterDto aboutPattern not null and not blank than return true")
        void whenUserFilterDtoSpecifiedAboutPatternNotNullAndNotBlankThenReturnTrue() {
            userFilterDto = UserFilterDto.builder()
                    .aboutPattern(ABOUT_PATTERN)
                    .build();

            assertTrue(userAboutFilter.isApplicable(userFilterDto));
        }

        @Test
        @DisplayName("If UserFilterDto aboutPattern not null and not blank than return sorted list")
        void whenUserFilterDtoSpecifiedAboutPatternThenReturnFilteredList() {
            Stream<User> userStream = Stream.of(
                    User.builder()
                            .aboutMe(ABOUT_PATTERN)
                            .build(),
                    User.builder()
                            .aboutMe("false")
                            .build());

            userFilterDto = UserFilterDto.builder()
                    .aboutPattern(ABOUT_PATTERN)
                    .build();

            Stream<User> userStreamAfterFilter = Stream.of(
                    User.builder()
                            .aboutMe(ABOUT_PATTERN)
                            .build());
            assertEquals(userStreamAfterFilter.toList(), userAboutFilter.apply(userStream, userFilterDto).toList());
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("If UserFilterDto aboutPattern is blank than return false")
        void whenUserFilterDtoAboutPatternIsBlankThenReturnFalse() {
            userFilterDto = UserFilterDto.builder()
                    .aboutPattern("   ")
                    .build();

            assertFalse(userAboutFilter.isApplicable(userFilterDto));
        }

        @Test
        @DisplayName("If UserFilterDto aboutPattern is null than return false")
        void whenUserFilterDtoAboutPatternIsNullThenReturnFalse() {
            userFilterDto = UserFilterDto.builder()
                    .aboutPattern(null)
                    .build();

            assertFalse(userAboutFilter.isApplicable(userFilterDto));
        }
    }
}