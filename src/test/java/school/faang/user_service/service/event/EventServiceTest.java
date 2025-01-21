package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventService eventService;

    private EventDto eventDto;
    private Event event;
    private User user;
    private Skill skill;

    @BeforeEach
    void setUpBeforeEachTest() {
        eventDto = new EventDto();
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkills(List.of(1L));
        eventDto.setId(1L);


        event = new Event();
        event.setId(1L);

        user = new User();
        user.setId(1L);

        skill = new Skill();
        skill.setId(1L);
    }

    private void mockUserRepository(Optional<User> user) {
        when(userRepository.findById(anyLong())).thenReturn(user);
    }

    private void mockSkillRepository(List<Skill> skills) {
        when(skillRepository.findAllById(anyList())).thenReturn(skills);
    }

    private void mockEventRepository(Optional<Event> event) {
        when(eventRepository.findById(anyLong())).thenReturn(event);
    }

    @Test
    void testCreateEvent() {
        mockUserRepository(Optional.of(user));
        mockSkillRepository(List.of(skill));
        when(eventMapper.toEntity(any(EventDto.class))).thenReturn(event);
        when(eventMapper.toDto(any(Event.class))).thenReturn(eventDto);
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        EventDto result = eventService.create(eventDto);

        assertNotNull(result);
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void testCreateEvent_UserNotFound() {
        mockUserRepository(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.create(eventDto));
    }

    @Test
    void testCreateEvent_SkillValidationFails() {
        mockUserRepository(Optional.of(user));
        mockSkillRepository(Collections.emptyList());

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    void testGetEvent() {
        mockEventRepository(Optional.of(event));
        when(eventMapper.toDto(any(Event.class))).thenReturn(eventDto);

        EventDto result = eventService.getEvent(1L);

        assertNotNull(result);
        verify(eventRepository).findById(anyLong());
    }

    @Test
    void testGetEventNotFound() {
        mockEventRepository(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.getEvent(1L));
    }

    @Test
    void testGetEventsByFilter() {
        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(eventMapper.toDto(any(Event.class))).thenReturn(eventDto);

        EventFilterDto filter = new EventFilterDto();
        List<EventDto> result = eventService.getEventsByFilter(filter);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(eventRepository).findAll();
    }

    @Test
    void testDeleteEvent() {
        mockEventRepository(Optional.of(event));

        eventService.deleteEvent(1L);

        verify(eventRepository).delete(any(Event.class));
    }

    @Test
    void testDeleteEventNotFound() {
        mockEventRepository(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.deleteEvent(1L));
    }

    @Test
    void testUpdateEvent() {
        mockEventRepository(Optional.of(event));
        when(skillRepository.findAllByUserId(anyLong())).thenReturn(List.of(skill));
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.toDto(any(Event.class))).thenReturn(eventDto);

        EventDto result = eventService.updateEvent(eventDto);

        assertNotNull(result);
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void testUpdateEventNotFound() {
        mockEventRepository(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.updateEvent(eventDto));
    }

    @Test
    void testUpdateEvent_SkillValidationFails() {
        mockEventRepository(Optional.of(event));
        when(skillRepository.findAllByUserId(anyLong())).thenReturn(Collections.emptyList());

        assertThrows(DataValidationException.class, () -> eventService.updateEvent(eventDto));
    }

    @Test
    void testGetOwnedEvents() {
        when(eventRepository.findAllByUserId(anyLong())).thenReturn(List.of(event));
        when(eventMapper.toDto(any(Event.class))).thenReturn(eventDto);

        List<EventDto> result = eventService.getOwnedEvents(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(eventRepository).findAllByUserId(anyLong());
    }

    @Test
    void testGetParticipatedEvents() {
        when(eventRepository.findParticipatedEventsByUserId(anyLong())).thenReturn(List.of(event));
        when(eventMapper.toDto(any(Event.class))).thenReturn(eventDto);

        List<EventDto> result = eventService.getParticipatedEvents(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(eventRepository).findParticipatedEventsByUserId(anyLong());
    }

}