package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.EventMapperImpl;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;
    @Spy
    private EventMapperImpl eventMapper;
    @Mock
    private UserRepository userRepository;
    @Captor
    private ArgumentCaptor<Event> captor;

    @Test
    public void testCreateAbsentSkillsAtUser() {

        Skill skill1 = createSkill(1, "Title1");
        Skill skill2 = createSkill(2, "Title2");
        Skill skill3 = createSkill(3, "Title3");

        User user = new User();
        user.setSkills(List.of(skill1, skill2));
        user.setId(1);

        Event event = new Event();
        event.setId(1);
        event.setOwner(user);
        event.setRelatedSkills(List.of(skill3));

        EventDto eventDto = eventMapper.toDto(event);

        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(java.util.Optional.of(event.getOwner()));
        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
        verify(eventMapper, times(1)).toEntity(eventDto);
        verify(eventMapper).toDto(captor.capture());
    }

    @Test
    public void testCreateSaveEvent() {
        Skill skill1 = createSkill(1, "Title1");

        User user = new User();
        user.setSkills(List.of(skill1));
        user.setId(1);

        Event event = new Event();
        event.setId(1);
        event.setOwner(user);
        event.setRelatedSkills(List.of(skill1));

        EventDto eventDto = eventMapper.toDto(event);

        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(java.util.Optional.of(event.getOwner()));
        eventService.create(eventDto);
        verify(eventRepository, times(1)).save(captor.capture());
        Event eventCaptor = captor.getValue();
        assertEquals(eventDto.getId(), eventCaptor.getId());
    }

    @Test
    public void testGetEvent() {
        Event event = new Event();
        event.setId(1L);
        event.setOwner(new User());
        event.setRelatedSkills(List.of(new Skill(), new Skill()));

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        eventService.getEvent(event.getId());

        verify(eventRepository, times(1)).findById(event.getId());
        verify(eventMapper, times(1)).toDto(event);
    }

    @Test
    public void testDeleteEvent() {
        Event event = new Event();
        event.setId(1L);

        eventService.deleteEvent(event.getId());
        verify(eventRepository, times(1)).deleteById(event.getId());
    }

    @Test
    public void testUpdateEventWithAbsentUserBd() {
        Skill skill1 = createSkill(1, "Title1");

        User user = new User();
        user.setSkills(List.of(skill1));
        user.setId(1);

        Event event = new Event();
        event.setId(1);
        event.setOwner(user);
        event.setRelatedSkills(List.of(skill1));

        EventDto eventDto = eventMapper.toDto(event);

        assertThrows(DataValidationException.class, () -> eventService.updateEvent(eventDto));
    }

    @Test
    public void testEventUpdateNotOwner() {

        Skill skill1 = createSkill(1, "Title1");

        User user = new User();
        user.setSkills(List.of(skill1));
        user.setId(1);

        User user2 = new User();
        user2.setId(2);

        Event event = new Event();
        event.setId(1);
        event.setOwner(user2);
        event.setRelatedSkills(List.of(skill1));

        EventDto eventDto = eventMapper.toDto(event);

        System.out.println(eventDto.getOwnerId());

        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(user));

        assertThrows(DataValidationException.class, () -> eventService.updateEvent(eventDto));
    }

    @Test
    public void testEventUpdateAbsentSkillsAtUser() {
        Skill skill1 = createSkill(1, "Title1");
        Skill skill2 = createSkill(2, "Title2");

        User user = new User();
        user.setSkills(List.of(skill1));
        user.setId(1);

        User user2 = new User();
        user2.setId(2);

        Event event = new Event();
        event.setId(1);
        event.setOwner(user2);
        event.setRelatedSkills(List.of(skill2));

        EventDto eventDto = eventMapper.toDto(event);

        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(user));
        assertThrows(DataValidationException.class, () -> eventService.updateEvent(eventDto));
    }

    @Test
    public void testGetOwnedEvents() {
        User user = new User();
        user.setId(1L);

        eventService.getOwnedEvents(user.getId());
        verify(eventRepository, times(1)).findAllByUserId(user.getId());
    }

    @Test
    public void testGetParticipatedEvents() {
        User user = new User();
        user.setId(1L);

        eventService.getParticipatedEvents(user.getId());
        verify(eventRepository, times(1)).findParticipatedEventsByUserId(user.getId());
    }

    private Skill createSkill(long id, String title) {
        Skill skill = new Skill();
        skill.setId(id);
        skill.setTitle(title);

        return skill;
    }
}