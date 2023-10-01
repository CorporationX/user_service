package school.faang.user_service.service;


import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.event.EventIdFilter;
import school.faang.user_service.filters.event.EventOwnerIdFilter;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.publisher.EventStartEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.filters.event.EventFilter;
import school.faang.user_service.service.event.EventService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventStartEventPublisher eventStartEventPublisher;
    @Spy
    private EventMapperImpl eventMapper;
    @InjectMocks
    private EventService eventService;

    private User user1 = User.builder()
            .id(1L)
            .skills(
                    List.of(
                            Skill.builder().id(2L).title("2").build(),
                            Skill.builder().id(3L).title("3").build()
                    )
            )
            .build();
    private User user2 = User.builder()
            .id(1L)
            .skills(
                    List.of(
                            Skill.builder().id(1L).title("1").build(),
                            Skill.builder().id(2L).title("2").build()
                    )
            )
            .build();

    private EventDto eventDto = EventDto.builder()
            .relatedSkills(
                    new ArrayList<>(List.of(
                            SkillDto.builder().id(1L).title("1").build(),
                            SkillDto.builder().id(2L).title("2").build()
                    ))
            )
            .ownerId(1L)
            .id(1L)
            .build();
    private Event event = Event.builder()
            .relatedSkills(
                    new ArrayList<>(List.of(
                            Skill.builder().id(1L).title("1").build(),
                            Skill.builder().id(2L).title("2").build()
                    ))
            )
            .id(1L)
            .owner(user2)
            .attendees(new ArrayList<>())
            .build();

    @Test
    public void testOwnerHasNoSkillsForEvent() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        Assertions.assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    public void testOwnerHasSkillsForEvent() {
        when(eventRepository.save(eventMapper.toEvent(eventDto))).thenReturn(event);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user2));

        eventService.create(eventDto);

        Mockito.verify(eventRepository, Mockito.times(1)).save(eventMapper.toEvent(eventDto));
        Mockito.verify(eventStartEventPublisher).publish(event.getId(), event.getStartDate());
    }

    @Test
    public void testGetEventWithWrongId() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> eventService.getEvent(0L));
        Assertions.assertThrows(EntityNotFoundException.class, () -> eventService.getEvent(-10L));
    }

    @Test
    public void testGetNonExistentEvent() {
        when(eventRepository.findById(10L)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> eventService.getEvent(10L));
    }

    @Test
    public void testCorrectGetEvent() {
        when(eventRepository.findById(1L)).thenReturn(Optional.ofNullable(event));
        Assertions.assertEquals(eventMapper.toDTO(event), eventService.getEvent(1L));
    }

    @Test
    public void testDeleteEvent() {
        eventService.deleteEvent(1L);
        Mockito.verify(eventRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void testUpdateEventOwnerHasNoSkills() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        Assertions.assertThrows(DataValidationException.class, () -> eventService.updateEvent(eventDto));
    }

    @Test
    public void testUpdateEventOwnerHasSkills() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user2));
        when(eventRepository.findById(1L)).thenReturn(Optional.ofNullable(event));
        when(eventRepository.save(eventMapper.update(eventDto, event))).thenReturn(event);
        eventService.updateEvent(eventDto);
        Mockito.verify(eventRepository, Mockito.times(1)).save(eventMapper.update(eventDto, event));
    }

    @Test
    public void testUpdateEventReturnsNull() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user2));
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(DataValidationException.class, () -> eventService.updateEvent(eventDto));
    }

    @Test
    public void testGetOwnedEventsIsNull() {
        when(eventRepository.findAllByUserId(1L)).thenReturn(null);
        Assertions.assertEquals(0, eventService.getOwnedEvents(1L).size());
    }

    @Test
    public void testGetOwnedEvents() {
        List<Event> events = List.of(Event.builder().build());
        when(eventRepository.findAllByUserId(1L)).thenReturn(events);
        Assertions.assertEquals(1, eventService.getOwnedEvents(1L).size());
    }

    @Test
    public void testGetParticipatedEventsIsNull() {
        when(eventRepository.findParticipatedEventsByUserId(1L)).thenReturn(null);
        Assertions.assertEquals(0, eventService.getParticipatedEvents(1L).size());
    }

    @Test
    public void testGetParticipatedEvents() {
        List<Event> events = List.of(Event.builder().build());
        when(eventRepository.findParticipatedEventsByUserId(1L)).thenReturn(events);
        Assertions.assertEquals(1, eventService.getParticipatedEvents(1L).size());
    }

    @Test
    public void testGetEventsByFilter() {
        List<EventFilter> filters = List.of(new EventIdFilter(), new EventOwnerIdFilter());
        EventService testEventService = new EventService(eventRepository, userRepository, eventStartEventPublisher, eventMapper, filters);
        EventFilterDto filter = EventFilterDto.builder().eventId(1L).ownerId(1L).build();
        List<Event> events = List.of(
                Event.builder().id(1L).owner(User.builder().id(1L).build()).build(),
                Event.builder().id(2L).owner(User.builder().id(1L).build()).build(),
                Event.builder().id(1L).owner(User.builder().id(2L).build()).build()
        );
        when(eventRepository.findAll()).thenReturn(events);
        Assertions.assertEquals(1, testEventService.getEventsByFilter(filter).size());
    }
}
