package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.validators.UserValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidator userValidator;

    @Mock
    private EventService eventService;

    @Mock
    private MentorshipService mentorshipService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Test
    public void testDeactivateUser() {
        long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.of(User.builder().id(id).active(true).build()));
        when(eventService.getOwnedEvents(id)).thenReturn(List.of(EventDto.builder().id(id).build()));
        userService.deactivate(id);
        verify(userRepository, times(1)).save(userArgumentCaptor.capture());
        verify(eventService, times(1)).deleteEvent(id);
        User capturedUser = userArgumentCaptor.getValue();
        assertFalse(capturedUser.isActive());
    }
}
