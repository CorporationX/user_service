package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.mapper.event.MapperEvent;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private MapperEvent mapperEvent;

    @InjectMocks
    private EventService eventService;

    @Test
    void getUsers() {
        assertTrue(true);
    }
}