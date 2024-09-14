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

    @InjectMocks
    private UserEmailFilter userEmailFilter;

    private UserFilterDto userFilterDto;

    private final static String EMAIL_PATTERN = "email";

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Если у UserFilterDto заполнено поле emailPattern не null и не пустое, тогда возвращаем true")
        void whenUserFilterDtoSpecifiedEmailPatternNotNullAndNotBlankThenReturnTrue() {
            userFilterDto = UserFilterDto.builder()
                    .emailPattern(EMAIL_PATTERN)
                    .build();

            assertTrue(userEmailFilter.isApplicable(userFilterDto));
        }

        @Test
        @DisplayName("Если у UserFilterDto заполнено поле emailPattern, тогда возвращаем отфильтрованный список")
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
        @DisplayName("Если у UserFilterDto поле emailPattern пустое, тогда возвращаем false")
        void whenUserFilterDtoEmailPatternIsBlankThenReturnFalse() {
            userFilterDto = UserFilterDto.builder()
                    .emailPattern("   ")
                    .build();

            assertFalse(userEmailFilter.isApplicable(userFilterDto));
        }

        @Test
        @DisplayName("Если у UserFilterDto поле emailPattern null, тогда возвращаем false")
        void whenUserFilterDtoEmailPatternIsNullThenReturnFalse() {
            userFilterDto = UserFilterDto.builder()
                    .emailPattern(null)
                    .build();

            assertFalse(userEmailFilter.isApplicable(userFilterDto));
        }
    }
}