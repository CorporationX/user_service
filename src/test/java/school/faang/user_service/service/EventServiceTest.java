package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventFilter;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Spy
    private EventMapper eventMapper = Mappers.getMapper(EventMapper.class);

    @Mock
    private List<EventFilter> eventFilters;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @BeforeEach
    public void setUp() {
        eventService = new EventService(eventRepository, userRepository, eventMapper, eventFilters, skillRepository);
    }

    @Test
    public void test() {
        EventDto eventDto = new EventDto(8L, "title", LocalDateTime.now(),
                LocalDateTime.now(), 1L, "description",
                List.of(), "location", 10, EventType.WEBINAR, EventStatus.COMPLETED);
        eventService.create(eventDto);
        Mockito.verify(eventRepository).save(Mockito.any(Event.class));
    }


}
