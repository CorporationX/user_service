package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private EventService eventService;


    @Test
    void should() {
        User user = User.builder().id(1L).build();
        EventDto event = EventDto.builder().id(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        eventService.create(event);
        verify(userRepository, times(1)).findById(1L);

    }

    @Test
    void create() {

    }


    @Test
    void validation() {
    }
}