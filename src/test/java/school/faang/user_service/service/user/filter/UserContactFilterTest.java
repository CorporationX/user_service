package school.faang.user_service.service.user.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.entity.contact.ContactType;
import school.faang.user_service.service.user.UserFilter;

import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserContactFilterTest {
    private UserFilter userContactFilter;
    private String contactTest;
    private List<Contact> contacts;

    @BeforeEach
    void setUp() {
        userContactFilter = new UserContactFilter();
        contactTest = ContactType.WHATSAPP.name();
        contacts = List.of(
                new Contact(1L, new User(), ContactType.WHATSAPP.name(), ContactType.WHATSAPP),
                new Contact(2L, new User(), ContactType.TELEGRAM.name(), ContactType.TELEGRAM)
        );
    }

    @Test
    void testIsApplicable_patternWithFilledContact() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setContactPattern(contactTest);
        boolean isApplicable = userContactFilter.isApplicable(userFilterDto);

        assertTrue(isApplicable);
    }

    @Test
    void testIsApplicable_patternWithNotFilledContact() {
        UserFilterDto userFilterDto = new UserFilterDto();
        boolean isApplicable = userContactFilter.isApplicable(userFilterDto);

        assertFalse(isApplicable);
    }

    @Test
    void testGetPredicate_successContactValidation() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setContactPattern(contactTest);

        User user = new User();
        user.setContacts(contacts);

        Predicate<User> predicate =  userContactFilter.getPredicate(userFilterDto);

        boolean result = predicate.test(user);

        assertTrue(result);
    }

    @Test
    void testGetPredicate_failedContactValidation() {
        List<Contact> contacts1 = List.of(
                new Contact(4L, new User(), ContactType.FACEBOOK.name(), ContactType.FACEBOOK),
                new Contact(2L, new User(), ContactType.TELEGRAM.name(), ContactType.TELEGRAM)
        );

        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setContactPattern(contactTest);

        User user = new User();
        user.setContacts(contacts1);

        Predicate<User> predicate =  userContactFilter.getPredicate(userFilterDto);

        boolean result = predicate.test(user);

        assertFalse(result);
    }
}
