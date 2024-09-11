package school.faang.user_service.filter.userFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserContactFilterTest {
    private UserContactFilter userContactFilter;

    @BeforeEach
    void setUp() {
        userContactFilter = new UserContactFilter();
    }

    @Test
    @DisplayName("Test isApplicable with non-null about pattern")
    public void testIsApplicable_withNonNullAboutPattern() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setContactPattern("test");

        assertTrue(userContactFilter.isApplicable(userFilterDto));
    }

    @Test
    @DisplayName("Test isApplicable with null about pattern")
    public void testIsApplicable_withNullAboutPattern() {
        UserFilterDto userFilterDto = new UserFilterDto();

        assertFalse(userContactFilter.isApplicable(userFilterDto));
    }

    @Test
    @DisplayName("Test apply with matching pattern")
    public void testApply_WithMatchingPattern() {
        Contact testContact = mock(Contact.class);
        when(testContact.getContact()).thenReturn("test");
        Contact anotherContact = mock(Contact.class);
        when(anotherContact.getContact()).thenReturn("another");

        UserFilterDto userFilterDto = mock(UserFilterDto.class);
        when(userFilterDto.getContactPattern()).thenReturn("test");

        User firstUser = mock(User.class);
        List<Contact> firstContacts = new ArrayList<>();
        firstContacts.add(testContact);
        when(firstUser.getContacts()).thenReturn(firstContacts);

        User secondUser = mock(User.class);
        List<Contact> secondContacts = new ArrayList<>();
        secondContacts.add(anotherContact);
        when(secondUser.getContacts()).thenReturn(secondContacts);

        List<User> users = new ArrayList<>();
        users.add(firstUser);
        users.add(secondUser);

        List<User> filteredUsers = users.stream()
                .filter(user -> user.getContacts().stream()
                        .anyMatch(contact -> contact.getContact().equals(userFilterDto.getContactPattern()))).toList();

        assertEquals(1, filteredUsers.size());
        assertEquals("test", filteredUsers.get(0).getContacts().get(0).getContact());
        assertEquals(filteredUsers, userContactFilter.apply(users, userFilterDto).toList());
    }

    @Test
    @DisplayName("Test apply with non-matching pattern")
    public void testApply_WithNonMatchingPattern() {
        Contact testContact = mock(Contact.class);
        when(testContact.getContact()).thenReturn("test");
        Contact anotherContact = mock(Contact.class);
        when(anotherContact.getContact()).thenReturn("another");

        UserFilterDto userFilterDto = mock(UserFilterDto.class);
        when(userFilterDto.getContactPattern()).thenReturn("nonmatching");

        User firstUser = mock(User.class);
        List<Contact> firstContacts = new ArrayList<>();
        firstContacts.add(testContact);
        when(firstUser.getContacts()).thenReturn(firstContacts);

        User secondUser = mock(User.class);
        List<Contact> secondContacts = new ArrayList<>();
        secondContacts.add(anotherContact);
        when(secondUser.getContacts()).thenReturn(secondContacts);

        List<User> users = new ArrayList<>();
        users.add(firstUser);
        users.add(secondUser);

        List<User> filteredUsers = users.stream()
                .filter(user -> user.getContacts().stream()
                        .anyMatch(contact -> contact.getContact().equals(userFilterDto.getContactPattern())))
                .toList();

        assertEquals(0, filteredUsers.size());
        assertEquals(filteredUsers, userContactFilter.apply(users, userFilterDto).toList());
    }
}
