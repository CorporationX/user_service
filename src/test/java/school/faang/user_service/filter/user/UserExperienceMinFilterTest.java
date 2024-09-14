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

    @InjectMocks
    private UserExperienceMinFilter userExperienceMinFilter;

    private UserFilterDto userFilterDto;

    private final static int EXPERIENCE_MIN_PATTERN = 2;
    private final static int EXPERIENCE_LESS_THAN_MIN_PATTERN = 1;
    private final static int EXPERIENCE_ZERO = 0;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Если у UserFilterDto поле experienceMin больше нуля, тогда возвращаем true")
        void whenUserFilterDtoSpecifiedExperienceMinMoreZanZeroThenReturnTrue() {
            userFilterDto = UserFilterDto.builder()
                    .experienceMin(EXPERIENCE_MIN_PATTERN)
                    .build();

            assertTrue(userExperienceMinFilter.isApplicable(userFilterDto));
        }

        @Test
        @DisplayName("Если у UserFilterDto заполнено поле experienceMin, тогда возвращаем отфильтрованный список")
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
        @DisplayName("Если у UserFilterDto поле experienceMin меньше нуля, тогда возвращаем false")
        void whenUserFilterDtoExperienceMinIsLessThanZeroThenReturnFalse() {
            userFilterDto = UserFilterDto.builder()
                    .experienceMax(EXPERIENCE_ZERO)
                    .build();

            assertFalse(userExperienceMinFilter.isApplicable(userFilterDto));
        }
    }
}