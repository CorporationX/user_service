package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @InjectMocks
    private EventService eventService;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventMapper eventMapper;

    @Test
    public void testCreateWithoutUserSkills() {
        EventDto eventDto = new EventDto();
        eventDto.setOwnerId(1L);
        Event event = new Event();
        event.setRelatedSkills(List.of(new Skill()));
        User user = new User();
        user.setSkills(Collections.emptyList());

        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(user));
        when(eventMapper.toEntity(eventDto, userRepository)).thenReturn(event);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
        assertEquals("User hasn't required skills", exception.getMessage());
        verify(eventMapper, times(0)).toDto(eventRepository.save(event));
    }

    @Test
    public void testCreateWithRelatedSkills() {
        EventDto eventDto = new EventDto();
        eventDto.setOwnerId(1L);
        Event event = new Event();
        Skill skill = new Skill();
        event.setRelatedSkills(List.of(skill));
        User user = new User();
        user.setSkills(List.of(skill));

        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(user));
        when(eventMapper.toEntity(eventDto, userRepository)).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(eventDto);
        when(eventRepository.save(event)).thenReturn(event);

        eventService.create(eventDto);

        verify(eventRepository, times(1)).save(event);
    }

    @Test
    public void getEventNotExisting() {
        long eventId = 1L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> eventService.getEvent(eventId));
        assertEquals("Event not found for ID: " + eventId, exception.getMessage());
    }

    @Test
    public void getEventExisting() {
        long eventId = 1L;
        User owner = new User();
        long ownerId = 2L;
        owner.setId(ownerId);
        Event event = new Event();
        event.setId(eventId);
        event.setOwner(owner);
        EventDto eventDto = new EventDto();
        eventDto.setId(eventId);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventMapper.toDto(any(Event.class))).thenReturn(eventDto);

        EventDto result = eventService.getEvent(eventId);

        assertNotNull(result);
        assertEquals(eventId, result.getId());
        verify(eventRepository, times(1)).findById(eventId);
    }

}
