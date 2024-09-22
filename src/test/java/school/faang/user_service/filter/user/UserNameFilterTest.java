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
class UserNameFilterTest {

    private final static String NAME_PATTERN = "name";

    @InjectMocks
    private UserNameFilter userNameFilter;

    private UserFilterDto userFilterDto;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("If UserFilterDto namePattern not null and not blank than return true")
        void whenUserFilterDtoSpecifiedNamePatternNotNullAndNotBlankThenReturnTrue() {
            userFilterDto = UserFilterDto.builder()
                    .namePattern(NAME_PATTERN)
                    .build();

            assertTrue(userNameFilter.isApplicable(userFilterDto));
        }

        @Test
        @DisplayName("If UserFilterDto namePattern not null and not blank than return sorted list")
        void whenUserFilterDtoSpecifiedNamePatternThenReturnFilteredList() {
            Stream<User> userStream = Stream.of(
                    User.builder()
                            .username(NAME_PATTERN)
                            .build(),
                    User.builder()
                            .username("false")
                            .build());

            userFilterDto = UserFilterDto.builder()
                    .namePattern(NAME_PATTERN)
                    .build();

            Stream<User> userStreamAfterFilter = Stream.of(
                    User.builder()
                            .username(NAME_PATTERN)
                            .build());
            assertEquals(userStreamAfterFilter.toList(), userNameFilter.apply(userStream, userFilterDto).toList());
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("If UserFilterDto namePattern is blank than return false")
        void whenUserFilterDtoNamePatternIsBlankThenReturnFalse() {
            userFilterDto = UserFilterDto.builder()
                    .aboutPattern("   ")
                    .build();

            assertFalse(userNameFilter.isApplicable(userFilterDto));
        }

        @Test
        @DisplayName("If UserFilterDto namePattern is null than return false")
        void whenUserFilterDtoNamePatternIsNullThenReturnFalse() {
            userFilterDto = UserFilterDto.builder()
                    .namePattern(null)
                    .build();

            assertFalse(userNameFilter.isApplicable(userFilterDto));
        }
    }
}