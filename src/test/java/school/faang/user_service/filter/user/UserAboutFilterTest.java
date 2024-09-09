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
class UserAboutFilterTest {

    @Spy
    private UserAboutFilter userAboutFilter;

    private UserFilterDto userFilterDto;

    private final String ABOUT_PATTERN = "about";

    @Nested
    class PositiveTests {

        @Nested
        class IsApplicable {

            @Test
            @DisplayName("Если у UserFilterDto заполнено поле aboutPattern не null и не пустое, тогда возвращаем true")
            void whenUserFilterDtoSpecifiedAboutPatternNotNullAndNotBlankThenReturnTrue() {
                userFilterDto = UserFilterDto.builder()
                        .aboutPattern(ABOUT_PATTERN)
                        .build();

                assertTrue(userAboutFilter.isApplicable(userFilterDto));
            }
        }

        @Nested
        class Apply {

            @Test
            @DisplayName("Если у UserFilterDto заполнено поле aboutPattern, тогда возвращаем отфильтрованный список")
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
    }

    @Nested
    class NegativeTests {

        @Nested
        class IsApplicable {

            @Test
            @DisplayName("Если у UserFilterDto поле aboutPattern пустое, тогда возвращаем false")
            void whenUserFilterDtoAboutPatternIsBlankThenReturnFalse() {
                userFilterDto = UserFilterDto.builder()
                        .aboutPattern("   ")
                        .build();

                assertFalse(userAboutFilter.isApplicable(userFilterDto));
            }

            @Test
            @DisplayName("Если у UserFilterDto поле aboutPattern null, тогда возвращаем false")
            void whenUserFilterDtoAboutPatternIsNullThenReturnFalse() {
                userFilterDto = UserFilterDto.builder()
                        .aboutPattern(null)
                        .build();

                assertFalse(userAboutFilter.isApplicable(userFilterDto));
            }
        }
    }
}