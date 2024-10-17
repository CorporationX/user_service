package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.model.filter_dto.UserFilterDto;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.Contact;
import school.faang.user_service.model.enums.ContactType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContactFilterStrategyTest {

    private ContactFilterStrategy contactFilterStrategy;

    private static final String CONTACT_1 = "123456";
    private static final String CONTACT_2 = "987654";
    private static final String USERNAME_1 = "user1";
    private static final String USERNAME_2 = "user2";
    private static final String PATTERN_MATCH = "123";
    private static final String PATTERN_NO_MATCH = "555";

    @BeforeEach
    void setUp() {
        contactFilterStrategy = new ContactFilterStrategy();
    }

    private User createUser(long id, String username, List<Contact> contacts) {
        User user = User.builder()
                .id(id)
                .username(username)
                .build();
        user.setContacts(contacts);
        return user;
    }

    private Contact createContact(long id, User user, String contact, ContactType type) {
        return Contact.builder()
                .id(id)
                .user(user)
                .contact(contact)
                .type(type)
                .build();
    }

    private UserFilterDto createFilterWithContactPattern(String pattern) {
        UserFilterDto filter = new UserFilterDto();
        filter.setContactPattern(pattern);
        return filter;
    }

    @Test
    void testApplyFilter_WithNonEmptyPattern() {
        UserFilterDto filter = createFilterWithContactPattern(PATTERN_MATCH);
        assertTrue(contactFilterStrategy.applyFilter(filter));
    }

    @Test
    void testApplyFilter_WithEmptyPattern() {
        UserFilterDto filter = createFilterWithContactPattern("");
        assertFalse(contactFilterStrategy.applyFilter(filter));
    }

    @Test
    void testApplyFilter_WithNullPattern() {
        UserFilterDto filter = createFilterWithContactPattern(null);
        assertFalse(contactFilterStrategy.applyFilter(filter));
    }

    @Test
    void testFilter_WithMatchingPattern() {
        User user1 = createUser(1L, USERNAME_1, List.of(createContact(1L, null, CONTACT_1, ContactType.GITHUB)));
        User user2 = createUser(2L, USERNAME_2, List.of(createContact(2L, null, CONTACT_2, ContactType.GITHUB)));
        List<User> users = List.of(user1, user2);

        UserFilterDto filter = createFilterWithContactPattern(PATTERN_MATCH);
        List<User> filteredUsers = contactFilterStrategy.filter(users, filter);

        assertEquals(1, filteredUsers.size());
        assertEquals(USERNAME_1, filteredUsers.get(0).getUsername());
    }

    @Test
    void testFilter_WithNoMatchingPattern() {
        User user1 = createUser(1L, USERNAME_1, List.of(createContact(1L, null, CONTACT_1, ContactType.GITHUB)));
        User user2 = createUser(2L, USERNAME_2, List.of(createContact(2L, null, CONTACT_2, ContactType.GITHUB)));
        List<User> users = List.of(user1, user2);

        UserFilterDto filter = createFilterWithContactPattern(PATTERN_NO_MATCH);
        List<User> filteredUsers = contactFilterStrategy.filter(users, filter);

        assertTrue(filteredUsers.isEmpty());
    }
}
