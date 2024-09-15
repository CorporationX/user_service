package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.filter.ContactFilter;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContactFilterTest {

    private ContactFilter contactFilter;

    @BeforeEach
    public void setUp() {
        contactFilter = new ContactFilter();
    }

    @Test
    public void testIsApplicable_WithNullContactPattern() {
        UserFilterDto filter = mock(UserFilterDto.class);
        when(filter.getContactPattern()).thenReturn(null);

        boolean result = contactFilter.isApplicable(filter);

        assertFalse(result);
    }

    @Test
    public void testIsApplicable_WithContactPattern() {
        UserFilterDto filter = mock(UserFilterDto.class);
        when(filter.getContactPattern()).thenReturn("test");

        boolean result = contactFilter.isApplicable(filter);

        assertTrue(result);
    }

    @Test
    public void testApply_WithMatchingContacts() {
        Contact contact1 = new Contact();
        contact1.setContact("test1@example.com");
        Contact contact2 = new Contact();
        contact2.setContact("other@example.com");
        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);

        UserFilterDto filter = mock(UserFilterDto.class);
        when(filter.getContactPattern()).thenReturn("test");
        when(user1.getContacts()).thenReturn(List.of(contact1, contact2));
        when(user2.getContacts()).thenReturn(List.of(contact2));

        List<User> userList = Arrays.asList(user1, user2);
        Stream<User> userStream = userList.stream();

        Stream<User> resultStream = contactFilter.apply(userStream, filter);
        List<User> resultList = resultStream.toList();

        assertEquals(1, resultList.size());
    }

    @Test
    public void testApply_WithNoMatchingContacts() {
        Contact contact1 = new Contact();
        contact1.setContact("example@example.com");
        Contact contact2 = new Contact();
        contact2.setContact("example@example.com");
        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);

        UserFilterDto filter = mock(UserFilterDto.class);
        when(filter.getContactPattern()).thenReturn("test");
        when(user1.getContacts()).thenReturn(List.of(contact1));
        when(user2.getContacts()).thenReturn(List.of(contact2));

        List<User> userList = Arrays.asList(user1, user2);
        Stream<User> userStream = userList.stream();

        Stream<User> resultStream = contactFilter.apply(userStream, filter);
        List<User> resultList = resultStream.toList();

        assertEquals(0, resultList.size());
    }

}
