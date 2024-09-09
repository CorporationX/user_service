package school.faang.user_service.filter.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserContactFilterTest {

    @Spy
    private UserContactFilter userContactFilter;

    private UserFilterDto userFilterDto;

    private final String CONTACT_PATTERN = "contact";

    @Nested
    class PositiveTests {

        @Nested
        class IsApplicable {

            @Test
            @DisplayName("Если у UserFilterDto заполнено поле contactPattern не null и не пустое, тогда возвращаем true")
            void whenUserFilterDtoSpecifiedContactPatternNotNullAndNotBlankThenReturnTrue() {
                userFilterDto = UserFilterDto.builder()
                        .contactPattern(CONTACT_PATTERN)
                        .build();

                assertTrue(userContactFilter.isApplicable(userFilterDto));
            }
        }

        @Nested
        class Apply {

            @Test
            @DisplayName("Если у UserFilterDto заполнено поле contactPattern, тогда возвращаем отфильтрованный список")
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
    }

    @Nested
    class NegativeTests {

        @Nested
        class IsApplicable {

            @Test
            @DisplayName("Если у UserFilterDto поле contactPattern пустое, тогда возвращаем false")
            void whenUserFilterDtoContactPatternIsBlankThenReturnFalse() {
                userFilterDto = UserFilterDto.builder()
                        .contactPattern("   ")
                        .build();

                assertFalse(userContactFilter.isApplicable(userFilterDto));
            }

            @Test
            @DisplayName("Если у UserFilterDto поле contactPattern null, тогда возвращаем false")
            void whenUserFilterDtoContactPatternIsNullThenReturnFalse() {
                userFilterDto = UserFilterDto.builder()
                        .contactPattern(null)
                        .build();

                assertFalse(userContactFilter.isApplicable(userFilterDto));
            }
        }
    }
}