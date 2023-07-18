package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventService eventService;

    private EventDto createEventDto() {
        EventDto eventDto = new EventDto();
        eventDto.setId(1L);
        eventDto.setTitle("Test Event");
        eventDto.setStartDate(LocalDate.of(2020, 1, 1).atStartOfDay());
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkills(List.of(new SkillDto(1L, "A"), new SkillDto(2L, "B")));
        return eventDto;
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setSkills(createSkills());
        return user;
    }

    private Event createEvent() {
        Event event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");
        event.setStartDate(LocalDate.of(2020, 1, 1).atStartOfDay());
        event.setOwner(createUser());
        event.setRelatedSkills(createSkills());
        return event;
    }

    private List<Skill> createSkills() {
        Skill skill1 = new Skill();
        skill1.setId(1L);
        skill1.setTitle("A");
        Skill skill2 = new Skill();
        skill2.setId(2L);
        skill2.setTitle("B");
        return List.of(skill1, skill2);
    }

    @Test
    public void invalid_EventId() {
        EventDto eventDto = createEventDto();
        eventDto.setId(0L);

        Exception exception = assertThrows(RuntimeException.class, () -> eventService.create(eventDto));
        assertEquals("java.util.zip.DataFormatException: Event Id must be greater than 0",
                exception.getMessage());
    }

    @Test
    public void invalid_TitleBlank() {
        EventDto eventDto = createEventDto();
        eventDto.setTitle("");

        Exception exception = assertThrows(RuntimeException.class, () -> eventService.create(eventDto));
        assertEquals("java.util.zip.DataFormatException: Event must have a title", exception.getMessage());
    }

    @Test
    public void invalid_NullStartDate() {
        EventDto eventDto = createEventDto();
        eventDto.setStartDate(null);

        Exception exception = assertThrows(RuntimeException.class, () -> eventService.create(eventDto));
        assertEquals("java.util.zip.DataFormatException: Event must have a start date", exception.getMessage());
    }

    @Test
    public void invalid_NullOwnerId() {
        EventDto eventDto = createEventDto();
        eventDto.setOwnerId(null);

        Exception exception = assertThrows(RuntimeException.class, () -> eventService.create(eventDto));
        assertEquals("java.util.zip.DataFormatException: Event must have a user", exception.getMessage());
    }

    @Test
    public void invalid_userNotContainsSkills() {
        EventDto eventDto = createEventDto();
        eventDto.setRelatedSkills(List.of(new SkillDto(3L, "C"), new SkillDto(2L, "B")));

        when(userRepository.findById(1L)).thenReturn(Optional.of(createUser()));

        Exception exception = assertThrows(RuntimeException.class, () -> eventService.create(eventDto));
        assertEquals("java.util.zip.DataFormatException: User has no related skills", exception.getMessage());

        verify(userRepository).findById(1L);
    }

    @Test
    void create_ShouldReturnEventDto() {
        Event event = createEvent();
        User user = createUser();
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);

        EventDto eventDto = createEventDto();
        EventDto createdEventDto = eventService.create(eventDto);

        assertEquals(eventDto.getTitle(), createdEventDto.getTitle());
        assertEquals(eventDto.getStartDate(), createdEventDto.getStartDate());
        assertEquals(eventDto.getOwnerId(), createdEventDto.getOwnerId());
        assertThat(eventDto.getRelatedSkills()).containsExactlyInAnyOrderElementsOf(createdEventDto.getRelatedSkills());

        verify(userRepository).findById(userId);
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void get_invalidEventId() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> eventService.get(99L));
        assertEquals("Event not found", exception.getMessage());
    }

    @Test
    void get_ShouldReturnEventDto() {
        Event event = createEvent();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        Long eventId = 1L;
        EventDto foundEventDto = eventService.get(eventId);

        assertNotNull(foundEventDto);
        assertEquals(event.getId(), foundEventDto.getId());

        verify(eventRepository, times(1)).findById(eventId);
    }

    @Test
    public void deleteEvent_EventExists() {
        Long eventId = 1L;
        when(eventRepository.existsById(eventId)).thenReturn(true);

        boolean result = eventService.deleteEvent(eventId);

        assertTrue(result);

        verify(eventRepository, times(1)).deleteById(eventId);
        verify(eventRepository, times(1)).existsById(eventId);
    }

    @Test
    public void deleteEvent_EventNotExist() {
        Long eventId = 1L;
        when(eventRepository.existsById(eventId)).thenReturn(false);

        boolean result = eventService.deleteEvent(eventId);

        assertFalse(result);

        verify(eventRepository, times(1)).existsById(eventId);
    }
}