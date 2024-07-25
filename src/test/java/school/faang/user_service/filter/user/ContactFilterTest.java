package school.faang.user_service.filter.user;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;

import java.util.List;

class ContactFilterTest {
    private User userFirst;
    private User userSecond;
    private User userThird;
    private Contact contactPhone;
    private Contact contactMail;
    private Contact contactBlueMail;
    private ContactFilter contactFilter;
    private UserFilterDto userFilterDto;

    @BeforeEach
    void setup() {
        contactPhone = new Contact();
        contactMail = new Contact();
        contactBlueMail = new Contact();
        userFirst = new User();
        userSecond = new User();
        userThird = new User();
        contactFilter = new ContactFilter();
        userFilterDto = mock(UserFilterDto.class);
    }

    @Test
    void testIsApplicableWhenContactPatternIsNotNull() {
        when(userFilterDto.getContactPattern()).thenReturn("phone");
        assertTrue(contactFilter.isApplicable(userFilterDto));
    }

    @Test
    void testIsApplicableWhenContactPatternIsNull() {
        when(userFilterDto.getContactPattern()).thenReturn(null);
        assertFalse(contactFilter.isApplicable(userFilterDto));
    }

    @Test
    void testApplyWhenUsersMatchContactPattern() {
        List<User> filteredUsers = getUserList();
        when(userFilterDto.getContactPattern()).thenReturn("phone");

        filteredUsers = contactFilter.apply(filteredUsers.stream(), userFilterDto).toList();
        assertEquals(2, filteredUsers.size());
        assertTrue(filteredUsers.contains(filteredUsers.get(0)));
        assertTrue(filteredUsers.contains(filteredUsers.get(1)));
    }

    @Test
    void testApplyWhenNoUsersMatchContactPattern() {
        List<User> filteredUsers = getUserList();
        when(userFilterDto.getContactPattern()).thenReturn("email");

        filteredUsers = contactFilter.apply(filteredUsers.stream(), userFilterDto).toList();
        assertEquals(0, filteredUsers.size());
        assertTrue(filteredUsers.isEmpty());
    }

    List<User> getUserList() {
        contactPhone.setContact("phone");
        contactMail.setContact("mail");
        contactBlueMail.setContact("blue mail");
        userFirst.setContacts(List.of(contactPhone, contactMail, contactBlueMail));
        userSecond.setContacts(List.of(contactPhone, contactMail, contactBlueMail));
        userThird.setContacts(List.of(contactMail, contactBlueMail));
        return List.of(userFirst, userSecond, userThird);
    }
}