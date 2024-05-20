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
import school.faang.user_service.validator.EventValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;
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
    @Spy
    private EventValidator eventValidator;

    private User userOne;
    private User userFirst;
    private User userSecond;
    private Event event;
    private EventDto eventDto;
    private Skill firstSkill;
    private Skill secondSkill;
    private Skill thirdSkill;
    private Long id;

    @BeforeEach
    public void setUp() {
        userOne = new User();
        userFirst = new User();
        userSecond = new User();
        event = new Event();
        eventDto = new EventDto();
        id = 1L;

        firstSkill = Skill.builder()
                .id(1)
                .title("FirstSkill")
                .build();

        secondSkill = Skill.builder()
                .id(2)
                .title("secondSkill")
                .build();

        thirdSkill = Skill.builder()
                .id(3)
                .title("thirdSkill")
                .build();
    }

    @Test
    public void testCreateAbsentSkillsAtUser() {
        userOne = User.builder()
                .skills(List.of(firstSkill, secondSkill))
                .id(id)
                .build();

        event = Event.builder()
                .id(id).owner(userOne)
                .relatedSkills(List.of(thirdSkill))
                .build();

        eventDto = eventMapper.toDto(event);

        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(java.util.Optional.of(event.getOwner()));
        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
        verify(eventMapper, times(1)).toEntity(eventDto);
        verify(eventMapper).toDto(captor.capture());
    }

    @Test
    public void testCreateSaveEvent() {
        userOne = User.builder()
                .skills(List.of(Skill.builder().id(1).build()))
                .id(id)
                .build();

        event = Event.builder()
                .id(id)
                .owner(userOne)
                .relatedSkills(List.of(Skill.builder().id(1).build()))
                .build();

        eventDto = eventMapper.toDto(event);

        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(java.util.Optional.of(event.getOwner()));

        eventService.create(eventDto);
        verify(eventRepository, times(1)).save(captor.capture());
        Event eventCaptor = captor.getValue();
        assertEquals(eventDto.getId(), eventCaptor.getId());
    }

    @Test
    public void testGetEvent() {
        event = Event.builder()
                .id(id)
                .owner(userOne)
                .relatedSkills(List.of(firstSkill, secondSkill))
                .build();

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        eventService.getEvent(event.getId());
        verify(eventRepository, times(1)).findById(event.getId());
        verify(eventMapper, times(1)).toDto(event);
    }

    @Test
    public void testDeleteEvent() {
        event.setId(id);

        eventService.deleteEvent(event.getId());
        verify(eventRepository, times(1)).deleteById(event.getId());
    }

    @Test
    public void testUpdateEventWithAbsentUserBd() {
        userOne = User.builder()
                .skills(List.of(firstSkill))
                .id(id)
                .build();

        event = Event.builder()
                .id(id)
                .owner(userOne)
                .relatedSkills(List.of(firstSkill))
                .build();

        eventDto = eventMapper.toDto(event);

        assertThrows(DataValidationException.class, () -> eventService.updateEvent(eventDto));
    }

    @Test
    public void testUpdateEventNotOwner() {
        userFirst.setId(id);
        userSecond.setId(2L);

        event.setOwner(userFirst);
        event.setRelatedSkills(List.of(
                Skill.builder().id(1).build(),
                Skill.builder().id(2).build(),
                Skill.builder().id(3).build()));


        eventDto = eventMapper.toDto(event);

        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(userSecond));
        assertThrows(DataValidationException.class, () -> eventService.updateEvent(eventDto));
    }

    @Test
    public void testEventUpdateAbsentSkillsAtUser() {
        userFirst = User.builder()
                .id(id)
                .skills(List.of(firstSkill))
                .build();

        userSecond.setId(2L);

        event = Event.builder()
                .id(id)
                .owner(userSecond)
                .relatedSkills(List.of(secondSkill))
                .build();

        eventDto = eventMapper.toDto(event);

        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(userFirst));
        assertThrows(DataValidationException.class, () -> eventService.updateEvent(eventDto));
    }

    @Test
    public void testGetOwnedEvents() {
        userOne.setId(id);

        eventService.getOwnedEvents(userOne.getId());
        verify(eventRepository, times(1)).findAllByUserId(userOne.getId());
    }

    @Test
    public void testGetParticipatedEvents() {
        userOne.setId(id);

        eventService.getParticipatedEvents(userOne.getId());
        verify(eventRepository, times(1)).findParticipatedEventsByUserId(userOne.getId());
    }

    @Test
    void failingTest() {
        fail("This test is meant to fail");
    }
}