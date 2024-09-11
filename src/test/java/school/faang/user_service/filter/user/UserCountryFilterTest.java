package school.faang.user_service.filter.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserCountryFilterTest {

    @InjectMocks
    private UserCountryFilter userCountryFilter;

    private UserFilterDto userFilterDto;

    private final static String COUNTRY_PATTERN = "country";

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Если у UserFilterDto заполнено поле countryPattern не null и не пустое, тогда возвращаем true")
        void whenUserFilterDtoSpecifiedCountryPatternNotNullAndNotBlankThenReturnTrue() {
            userFilterDto = UserFilterDto.builder()
                    .countryPattern(COUNTRY_PATTERN)
                    .build();

            assertTrue(userCountryFilter.isApplicable(userFilterDto));
        }

        @Test
        @DisplayName("Если у UserFilterDto заполнено поле countryPattern, тогда возвращаем отфильтрованный список")
        void whenUserFilterDtoSpecifiedCountryPatternThenReturnFilteredList() {
            Stream<User> userStream = Stream.of(
                    User.builder()
                            .country(Country.builder()
                                    .title(COUNTRY_PATTERN)
                                    .build())
                            .build(),
                    User.builder()
                            .country(Country.builder()
                                    .title("false")
                                    .build())
                            .build());

            userFilterDto = UserFilterDto.builder()
                    .countryPattern(COUNTRY_PATTERN)
                    .build();

            Stream<User> userStreamAfterFilter = Stream.of(
                    User.builder()
                            .country(Country.builder()
                                    .title(COUNTRY_PATTERN)
                                    .build())
                            .build());
            assertEquals(userStreamAfterFilter.toList(),
                    userCountryFilter.apply(userStream, userFilterDto).toList());
        }
    }

    @Nested
    class NegativeTests {

        @Nested
        class IsApplicable {

            @Test
            @DisplayName("Если у UserFilterDto поле countryPattern пустое, тогда возвращаем false")
            void whenUserFilterDtoCountryPatternIsBlankThenReturnFalse() {
                userFilterDto = UserFilterDto.builder()
                        .countryPattern("   ")
                        .build();

                assertFalse(userCountryFilter.isApplicable(userFilterDto));
            }

            @Test
            @DisplayName("Если у UserFilterDto поле countryPattern null, тогда возвращаем false")
            void whenUserFilterDtoCountryPatternIsNullThenReturnFalse() {
                userFilterDto = UserFilterDto.builder()
                        .countryPattern(null)
                        .build();

                assertFalse(userCountryFilter.isApplicable(userFilterDto));
            }
        }
    }
}