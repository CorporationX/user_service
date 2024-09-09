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
class UserCityFilterTest {

    @Spy
    private UserCityFilter userCityFilter;

    private UserFilterDto userFilterDto;

    private final String CITY_PATTERN = "city";

    @Nested
    class PositiveTests {

        @Nested
        class IsApplicable {

            @Test
            @DisplayName("Если у UserFilterDto заполнено поле cityPattern не null и не пустое, тогда возвращаем true")
            void whenUserFilterDtoSpecifiedCityPatternNotNullAndNotBlankThenReturnTrue() {
                userFilterDto = UserFilterDto.builder()
                        .cityPattern(CITY_PATTERN)
                        .build();

                assertTrue(userCityFilter.isApplicable(userFilterDto));
            }
        }

        @Nested
        class Apply {

            @Test
            @DisplayName("Если у UserFilterDto заполнено поле cityPattern, тогда возвращаем отфильтрованный список")
            void whenUserFilterDtoSpecifiedCityPatternThenReturnFilteredList() {
                Stream<User> userStream = Stream.of(
                        User.builder()
                                .city(CITY_PATTERN)
                                .build(),
                        User.builder()
                                .city("false")
                                .build());

                userFilterDto = UserFilterDto.builder()
                        .cityPattern(CITY_PATTERN)
                        .build();

                Stream<User> userStreamAfterFilter = Stream.of(
                        User.builder()
                                .city(CITY_PATTERN)
                                .build());
                assertEquals(userStreamAfterFilter.toList(), userCityFilter.apply(userStream, userFilterDto).toList());
            }
        }
    }

    @Nested
    class NegativeTests {

        @Nested
        class IsApplicable {

            @Test
            @DisplayName("Если у UserFilterDto поле cityPattern пустое, тогда возвращаем false")
            void whenUserFilterDtoCityPatternIsBlankThenReturnFalse() {
                userFilterDto = UserFilterDto.builder()
                        .cityPattern("   ")
                        .build();

                assertFalse(userCityFilter.isApplicable(userFilterDto));
            }

            @Test
            @DisplayName("Если у UserFilterDto поле cityPattern null, тогда возвращаем false")
            void whenUserFilterDtoCityPatternIsNullThenReturnFalse() {
                userFilterDto = UserFilterDto.builder()
                        .cityPattern(null)
                        .build();

                assertFalse(userCityFilter.isApplicable(userFilterDto));
            }
        }
    }
}