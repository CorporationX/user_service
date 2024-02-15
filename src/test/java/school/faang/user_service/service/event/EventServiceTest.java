package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.event.EventValidator;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserService userService;

    @Mock
    private EventValidator eventValidator;
    @Spy
    private EventMapper eventMapper = new EventMapperImpl();
    @Mock
    private List<EventFilter> eventFilterList;
    @InjectMocks
    private EventService eventService;

    //может ненужен mock для eventValidator
//    @Test
//    void shouldCreateEqualsEventService() {
//        EventDto eventDto = new EventDto();
//        User user = new User();
//        user.setSkills(List.of(new Skill(), new Skill()));
//        eventDto.setOwnerId(1l);
//        List<String> skillList = new ArrayList<>(List.of(new String("test")));
//        when(userService.getUser(eventDto.getOwnerId())).thenReturn(new User());
//        verify(eventValidator, times(1)).validate(skillList, user.getSkills());
//        when(eventMapper.toEntity(eventDto)).thenReturn(new Event());
//        Event event = new Event();
//        event.setId(1l);
//        when(eventRepository.save(any(Event.class))).thenReturn(new Event());
//        verify(eventRepository, times(1)).save(event);
//    }

    @Test
    void shouldGetEventService() {
        long eventId = 1;
        eventService.getEvent(eventId);
        verify(eventRepository, times(1)).findById(eventId);
    }
//    //Не знаю
//    @Test
//    void shouldGetEventsByFilterService() {
//        eventService.getEventsByFilter(new EventFilterDto());
//        verify(eventRepository, times(1)).findAll();
//    }

    @Test
    void shouldDeleteEventService() {
        long eventId = 1;
        eventService.deleteEvent(eventId);
        verify(eventRepository, times(1)).deleteById(eventId);
    }

}



