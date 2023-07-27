package school.faang.user_service.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.GoogleTokenRepository;
import school.faang.user_service.repository.event.EventRepository;

@ExtendWith(MockitoExtension.class)
class GoogleCalendarServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private GoogleTokenRepository googleTokenRepository;
    @InjectMocks
    private GoogleCalendarService googleCalendarService;

}