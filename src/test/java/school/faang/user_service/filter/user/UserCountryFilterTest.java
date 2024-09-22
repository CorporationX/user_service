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

    private final static String COUNTRY_PATTERN = "country";

    @InjectMocks
    private UserCountryFilter userCountryFilter;

    private UserFilterDto userFilterDto;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("If UserFilterDto countryPattern not null and not blank than return true")
        void whenUserFilterDtoSpecifiedCountryPatternNotNullAndNotBlankThenReturnTrue() {
            userFilterDto = UserFilterDto.builder()
                    .countryPattern(COUNTRY_PATTERN)
                    .build();

            assertTrue(userCountryFilter.isApplicable(userFilterDto));
        }

        @Test
        @DisplayName("If UserFilterDto countryPattern not null and not blank than return sorted list")
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

        @Test
        @DisplayName("If UserFilterDto countryPattern is blank than return false")
        void whenUserFilterDtoCountryPatternIsBlankThenReturnFalse() {
            userFilterDto = UserFilterDto.builder()
                    .countryPattern("   ")
                    .build();

            assertFalse(userCountryFilter.isApplicable(userFilterDto));
        }

        @Test
        @DisplayName("If UserFilterDto countryPattern is null than return false")
        void whenUserFilterDtoCountryPatternIsNullThenReturnFalse() {
            userFilterDto = UserFilterDto.builder()
                    .countryPattern(null)
                    .build();

            assertFalse(userCountryFilter.isApplicable(userFilterDto));
        }
    }
}