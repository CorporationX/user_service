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
class UserPhoneFilterTest {

    @InjectMocks
    private UserPhoneFilter userPhoneFilter;

    private UserFilterDto userFilterDto;

    private final static String PHONE_PATTERN = "phone";

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Если у UserFilterDto заполнено поле phonePattern не null и не пустое, тогда возвращаем true")
        void whenUserFilterDtoSpecifiedPhonePatternNotNullAndNotBlankThenReturnTrue() {
            userFilterDto = UserFilterDto.builder()
                    .phonePattern(PHONE_PATTERN)
                    .build();

            assertTrue(userPhoneFilter.isApplicable(userFilterDto));
        }

        @Test
        @DisplayName("Если у UserFilterDto заполнено поле phonePattern, тогда возвращаем отфильтрованный список")
        void whenUserFilterDtoSpecifiedPhonePatternThenReturnFilteredList() {
            Stream<User> userStream = Stream.of(
                    User.builder()
                            .phone(PHONE_PATTERN)
                            .build(),
                    User.builder()
                            .phone("false")
                            .build());

            userFilterDto = UserFilterDto.builder()
                    .phonePattern(PHONE_PATTERN)
                    .build();

            Stream<User> userStreamAfterFilter = Stream.of(
                    User.builder()
                            .phone(PHONE_PATTERN)
                            .build());
            assertEquals(userStreamAfterFilter.toList(), userPhoneFilter.apply(userStream, userFilterDto).toList());
        }
    }

    @Nested
    class NegativeTests {

        @Nested
        class IsApplicable {

            @Test
            @DisplayName("Если у UserFilterDto поле phonePattern пустое, тогда возвращаем false")
            void whenUserFilterDtoPhonePatternIsBlankThenReturnFalse() {
                userFilterDto = UserFilterDto.builder()
                        .phonePattern("   ")
                        .build();

                assertFalse(userPhoneFilter.isApplicable(userFilterDto));
            }

            @Test
            @DisplayName("Если у UserFilterDto поле phonePattern null, тогда возвращаем false")
            void whenUserFilterDtoPhonePatternIsNullThenReturnFalse() {
                userFilterDto = UserFilterDto.builder()
                        .phonePattern(null)
                        .build();

                assertFalse(userPhoneFilter.isApplicable(userFilterDto));
            }
        }
    }

}