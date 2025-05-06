package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.messages.ErrorMessages;
import school.faang.user_service.repository.contact.ContactPreferenceRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private ContactPreferenceRepository contactPreferenceRepository;

    @InjectMocks
    private UserService userService;

    private ContactPreference contactPreference;
    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        User user = new User();
        user.setId(userId);
        contactPreference = new ContactPreference();
        contactPreference.setUser(user);
        contactPreference.setPreference(PreferredContact.EMAIL);
    }

    @Test
    void testGetPreferredContact_Success() {
        when(contactPreferenceRepository.findByUserId(userId)).thenReturn(Optional.of(contactPreference));

        PreferredContact result = userService.getPreferredContact(userId);

        assertEquals(PreferredContact.EMAIL, result);
        verify(contactPreferenceRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testGetPreferredContact_UserNotFound() {
        when(contactPreferenceRepository.findByUserId(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.getPreferredContact(userId));

        assertEquals(ErrorMessages.getErrorNotFoundContact(userId), exception.getMessage());
        verify(contactPreferenceRepository, times(1)).findByUserId(userId);
    }
}
