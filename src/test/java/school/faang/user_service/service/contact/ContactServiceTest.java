package school.faang.user_service.service.contact;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.user.UserNotFoundException;
import school.faang.user_service.repository.contact.ContactPreferenceRepository;
import school.faang.user_service.service.user.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ContactServiceTest {
    @Mock
    private ContactPreferenceRepository contactPreferenceRepository;
    @Mock
    private UserService userService;
    @Captor
    private ArgumentCaptor<ContactPreference> contactCaptor;
    @InjectMocks
    private ContactService contactService;
    private User user;
    private ContactPreference contactPreference;

    @BeforeEach
    public void setUp() {
        long userId = 15L;
        user = new User();
        user.setId(userId);

        PreferredContact preference = PreferredContact.EMAIL;
        contactPreference = new ContactPreference();
        contactPreference.setId(3L);
        contactPreference.setUser(user);
        contactPreference.setPreference(preference);
    }

    @Test
    void testGetAllContact_returnAllPreferences() {
        List<ContactPreference> preferences = List.of(
                new ContactPreference(), new ContactPreference()
        );

        when(contactPreferenceRepository.findAll()).thenReturn(preferences);

        List<ContactPreference> result = contactService.getAllContact();

        assertEquals(2, result.size());
        verify(contactPreferenceRepository).findAll();
    }

    @Test
    void testSetPreferenceContactForUser_userNotFound() {
        when(userService.getUserByIdOrThrow(user.getId())).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> contactService.setPreferenceContactForUser(
                user.getId(),
                contactPreference.getPreference()
        ));

        verify(contactPreferenceRepository, never()).save(any());
    }

    @Test
    void testSetPreferenceContactForUser_saveAndReturnPreference() {
        when(userService.getUserByIdOrThrow(user.getId())).thenReturn(user);
        when(contactPreferenceRepository.save(contactCaptor.capture())).thenReturn(contactPreference);

        ContactPreference result = contactService.setPreferenceContactForUser(
                user.getId(),
                contactPreference.getPreference()
        );

        ContactPreference captureContactPreference = contactCaptor.getValue();
        assertEquals(contactPreference, result);
        assertEquals(captureContactPreference.getPreference(), result.getPreference());
        assertEquals(user, result.getUser());
        verify(userService).getUserByIdOrThrow(user.getId());
        verify(contactPreferenceRepository).save(captureContactPreference);
    }
}
