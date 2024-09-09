package school.faang.user_service.filter.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserExperienceMaxFilterTest {

    @Spy
    private UserExperienceMaxFilter userExperienceMaxFilter;

    private UserFilterDto userFilterDto;

    private final int EXPERIENCE_MAX_PATTERN = 10;
    private final int EXPERIENCE_MORE_THAN_MAX_PATTERN = 11;
    private final int EXPERIENCE_ZERO = 0;

    @Nested
    class PositiveTests {

        @Nested
        class IsApplicable {

            @Test
            @DisplayName("Если у UserFilterDto поле experienceMax больше нуля, тогда возвращаем true")
            void whenUserFilterDtoSpecifiedExperienceMaxMoreZanZeroThenReturnTrue() {
                userFilterDto = UserFilterDto.builder()
                        .experienceMax(EXPERIENCE_MAX_PATTERN)
                        .build();

                assertTrue(userExperienceMaxFilter.isApplicable(userFilterDto));
            }
        }

        @Nested
        class Apply {

            @Test
            @DisplayName("Если у UserFilterDto заполнено поле experienceMax, тогда возвращаем отфильтрованный список")
            void whenUserFilterDtoSpecifiedExperienceMaxThenReturnFilteredList() {
                Stream<User> userStream = Stream.of(
                        User.builder()
                                .experience(EXPERIENCE_MAX_PATTERN)
                                .build(),
                        User.builder()
                                .experience(EXPERIENCE_MORE_THAN_MAX_PATTERN)
                                .build());

                userFilterDto = UserFilterDto.builder()
                        .experienceMax(EXPERIENCE_MAX_PATTERN)
                        .build();

                Stream<User> userStreamAfterFilter = Stream.of(
                        User.builder()
                                .experience(EXPERIENCE_MAX_PATTERN)
                                .build());
                assertEquals(userStreamAfterFilter.toList(), userExperienceMaxFilter.apply(userStream, userFilterDto).toList());
            }
        }
    }

    @Nested
    class NegativeTests {

        @Nested
        class IsApplicable {

            @Test
            @DisplayName("Если у UserFilterDto поле experienceMax меньше нуля, тогда возвращаем false")
            void whenUserFilterDtoExperienceMaxIsLessThanZeroThenReturnFalse() {
                userFilterDto = UserFilterDto.builder()
                        .experienceMax(EXPERIENCE_ZERO)
                        .build();

                assertFalse(userExperienceMaxFilter.isApplicable(userFilterDto));
            }
        }
    }
}