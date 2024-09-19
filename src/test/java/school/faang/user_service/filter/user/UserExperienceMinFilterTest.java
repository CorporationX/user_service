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
class UserExperienceMinFilterTest {

    private final static int EXPERIENCE_MIN_PATTERN = 2;
    private final static int EXPERIENCE_LESS_THAN_MIN_PATTERN = 1;
    private final static int EXPERIENCE_ZERO = 0;

    @InjectMocks
    private UserExperienceMinFilter userExperienceMinFilter;

    private UserFilterDto userFilterDto;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("If UserFilterDto experienceMin positive than return true")
        void whenUserFilterDtoSpecifiedExperienceMinMoreZanZeroThenReturnTrue() {
            userFilterDto = UserFilterDto.builder()
                    .experienceMin(EXPERIENCE_MIN_PATTERN)
                    .build();

            assertTrue(userExperienceMinFilter.isApplicable(userFilterDto));
        }

        @Test
        @DisplayName("If UserFilterDto experienceMin positive than return sorted list")
        void whenUserFilterDtoSpecifiedExperienceMinThenReturnFilteredList() {
            Stream<User> userStream = Stream.of(
                    User.builder()
                            .experience(EXPERIENCE_MIN_PATTERN)
                            .build(),
                    User.builder()
                            .experience(EXPERIENCE_LESS_THAN_MIN_PATTERN)
                            .build());

            userFilterDto = UserFilterDto.builder()
                    .experienceMin(EXPERIENCE_MIN_PATTERN)
                    .build();

            Stream<User> userStreamAfterFilter = Stream.of(
                    User.builder()
                            .experience(EXPERIENCE_MIN_PATTERN)
                            .build());
            assertEquals(userStreamAfterFilter.toList(), userExperienceMinFilter.apply(userStream, userFilterDto).toList());
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("If UserFilterDto experienceMin less than zero than return false")
        void whenUserFilterDtoExperienceMinIsLessThanZeroThenReturnFalse() {
            userFilterDto = UserFilterDto.builder()
                    .experienceMax(EXPERIENCE_ZERO)
                    .build();

            assertFalse(userExperienceMinFilter.isApplicable(userFilterDto));
        }
    }
}