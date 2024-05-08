package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapperImpl;
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

    private User userOne;
    private User userFirst;
    private User userSecond;
    private Event event;
    private EventDto eventDto;
    private Skill firstSkill;
    private Skill secondSkill;
    private Skill thirdSkill;

    @BeforeEach
    public void setUp() {
        userOne = new User();
        userFirst = new User();
        userSecond = new User();
        event = new Event();
        eventDto = new EventDto();

        firstSkill = new Skill();
        firstSkill.setId(1);
        firstSkill.setTitle("FirstSkill");

        secondSkill = new Skill();
        secondSkill.setId(2);
        secondSkill.setTitle("SecondSkill");

        thirdSkill = new Skill();
        thirdSkill.setId(3);
        thirdSkill.setTitle("ThirdSkill");
    }

    @Test
    public void testCreateAbsentSkillsAtUser() {
        userOne.setSkills(List.of(firstSkill, secondSkill));
        userOne.setId(1L);

        event.setId(1L);
        event.setOwner(userOne);
        event.setRelatedSkills(List.of(thirdSkill));

        eventDto = eventMapper.toDto(event);

        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(java.util.Optional.of(event.getOwner()));
        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
        verify(eventMapper, times(1)).toEntity(eventDto);
        verify(eventMapper).toDto(captor.capture());
    }

    @Test
    public void testCreateSaveEvent() {
        userOne.setSkills(List.of(firstSkill));
        userOne.setId(1L);

        event.setId(1L);
        event.setOwner(userOne);
        event.setRelatedSkills(List.of(firstSkill));

        eventDto = eventMapper.toDto(event);

        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(java.util.Optional.of(event.getOwner()));
        eventService.create(eventDto);
        verify(eventRepository, times(1)).save(captor.capture());
        Event eventCaptor = captor.getValue();
        assertEquals(eventDto.getId(), eventCaptor.getId());
    }

    @Test
    public void testGetEvent() {
        event.setId(1L);
        event.setOwner(userOne);
        event.setRelatedSkills(List.of(firstSkill, secondSkill));

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        eventService.getEvent(event.getId());
        verify(eventRepository, times(1)).findById(event.getId());
        verify(eventMapper, times(1)).toDto(event);
    }

    @Test
    public void testDeleteEvent() {
        event.setId(1L);

        eventService.deleteEvent(event.getId());
        verify(eventRepository, times(1)).deleteById(event.getId());
    }

    @Test
    public void testUpdateEventWithAbsentUserBd() {
        userOne.setSkills(List.of(firstSkill));
        userOne.setId(1L);

        event.setId(1L);
        event.setOwner(userOne);
        event.setRelatedSkills(List.of(firstSkill));

        eventDto = eventMapper.toDto(event);

        assertThrows(DataValidationException.class, () -> eventService.updateEvent(eventDto));
    }

    @Test
    public void testUpdateEventNotOwner() {
        userFirst.setId(1L);
        userSecond.setId(2L);

        event.setOwner(userFirst);
        eventDto = eventMapper.toDto(event);

        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(userSecond));
        assertThrows(DataValidationException.class, () -> eventService.updateEvent(eventDto));
    }

    @Test
    public void testEventUpdateAbsentSkillsAtUser() {
        userFirst.setId(1L);
        userFirst.setSkills(List.of(firstSkill));

        userSecond.setId(2L);

        event.setId(1L);
        event.setOwner(userSecond);
        event.setRelatedSkills(List.of(secondSkill));

        eventDto = eventMapper.toDto(event);

        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(userFirst));
        assertThrows(DataValidationException.class, () -> eventService.updateEvent(eventDto));
    }

    @Test
    public void testGetOwnedEvents() {
        userOne.setId(1L);

        eventService.getOwnedEvents(userOne.getId());
        verify(eventRepository, times(1)).findAllByUserId(userOne.getId());
    }

    @Test
    public void testGetParticipatedEvents() {
        userOne.setId(1L);

        eventService.getParticipatedEvents(userOne.getId());
        verify(eventRepository, times(1)).findParticipatedEventsByUserId(userOne.getId());
    }
}