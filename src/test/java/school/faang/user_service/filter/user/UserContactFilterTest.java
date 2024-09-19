package school.faang.user_service.filter.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserContactFilterTest {

    private final static String CONTACT_PATTERN = "contact";

    @InjectMocks
    private UserContactFilter userContactFilter;

    private UserFilterDto userFilterDto;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("If UserFilterDto contactPattern not null and not blank than return true")
        void whenUserFilterDtoSpecifiedContactPatternNotNullAndNotBlankThenReturnTrue() {
            userFilterDto = UserFilterDto.builder()
                    .contactPattern(CONTACT_PATTERN)
                    .build();

            assertTrue(userContactFilter.isApplicable(userFilterDto));
        }

        @Test
        @DisplayName("If UserFilterDto contactPattern not null and not blank than return sorted list")
        void whenUserFilterDtoSpecifiedContactPatternThenReturnFilteredList() {
            List<Contact> contactsWithPattern = List.of(Contact.builder()
                    .contact(CONTACT_PATTERN)
                    .build());

            Stream<User> userStream = Stream.of(
                    User.builder()
                            .contacts(contactsWithPattern)
                            .build(),
                    User.builder()
                            .contacts(List.of(new Contact()))
                            .build());

            userFilterDto = UserFilterDto.builder()
                    .contactPattern(CONTACT_PATTERN)
                    .build();

            Stream<User> userStreamAfterFilter = Stream.of(
                    User.builder()
                            .contacts(contactsWithPattern)
                            .build());
            assertEquals(userStreamAfterFilter.toList(), userContactFilter.apply(userStream, userFilterDto).toList());
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("If UserFilterDto contactPattern is blank than return false")
        void whenUserFilterDtoContactPatternIsBlankThenReturnFalse() {
            userFilterDto = UserFilterDto.builder()
                    .contactPattern("   ")
                    .build();

            assertFalse(userContactFilter.isApplicable(userFilterDto));
        }

        @Test
        @DisplayName("If UserFilterDto contactPattern is null than return false")
        void whenUserFilterDtoContactPatternIsNullThenReturnFalse() {
            userFilterDto = UserFilterDto.builder()
                    .contactPattern(null)
                    .build();

            assertFalse(userContactFilter.isApplicable(userFilterDto));
        }
    }
}