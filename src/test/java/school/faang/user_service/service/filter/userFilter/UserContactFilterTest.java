package school.faang.user_service.service.filter.userFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.service.userFilter.UserContactFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
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
    public void testIsApplicable_withNonNullAboutPattern() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setContactPattern("test");

        assertTrue(userContactFilter.isApplicable(userFilterDto));
    }

    @Test
    public void testIsApplicable_withNullAboutPattern() {
        UserFilterDto userFilterDto = new UserFilterDto();

        assertFalse(userContactFilter.isApplicable(userFilterDto));
    }

    @Test
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

        List<User> users = List.of(firstUser, secondUser);
        Stream<User> userStream = users.stream();

        userContactFilter.apply(userStream, userFilterDto);

        List<User> filteredUsers = users.stream()
                        .filter(user -> user.getContacts().stream()
                .anyMatch(contact -> contact.getContact().equals(userFilterDto.getContactPattern()))).toList();

        assertEquals(1, filteredUsers.size());
        assertEquals("test", filteredUsers.get(0).getContacts().get(0).getContact());
    }

    @Test
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

        List<User> users = List.of(firstUser, secondUser);
        Stream<User> userStream = users.stream();

        userContactFilter.apply(userStream, userFilterDto);

        List<User> filteredUsers = users.stream()
                .filter(user -> user.getContacts().stream()
                        .anyMatch(contact -> contact.getContact().equals(userFilterDto.getContactPattern())))
                .toList();

        assertEquals(0, filteredUsers.size());
    }
}
