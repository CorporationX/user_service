package school.faang.user_service.service.event.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.EventCreationNotAllowedException;
import school.faang.user_service.exception.RecordNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validation.event.EventValidation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.event.EventStatus.PLANNED;
import static school.faang.user_service.entity.event.EventType.WEBINAR;
import static school.faang.user_service.util.LogsConstants.NOT_ENOUGH_SKILLS;
import static school.faang.user_service.util.LogsConstants.USER_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private EventValidation eventValidation;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private EventServiceImpl eventService;
    User user;
    User userWithoutSkills;
    Event event;
    Event eventWithoutSkills;
    List<Skill> skills;
    Skill skill1;
    Skill skill2;
    List<Long> skillIds;
    Long ownerId = 1L;
    Long userWithoutSkillsId = 2L;
    Long eventId = 1L;
    Long eventWithoutSkillsId = 2L;
    String eventTitle = "Test Event";
    String eventWithoutSkillsTitle = "Event without Skills";

    @BeforeEach
    void setUp() {
        skills = new ArrayList<>();
        skill1 = new Skill();
        skill1.setId(1L);
        skill1.setTitle("Java");
        skill2 = new Skill();
        skill2.setId(2L);
        skill2.setTitle("Spring");
        skills.add(skill1);
        skills.add(skill2);

        user = new User();
        user.setId(1L);
        user.setUsername("JohnDoe");
        user.setSkills(skills);

        userWithoutSkills = new User();
        userWithoutSkills.setId(userWithoutSkillsId);
        userWithoutSkills.setUsername("Michael Jackson");

        skillIds = Arrays.asList(1L, 2L);

        event = Event.builder()
                .id(eventId)
                .title(eventTitle)
                .description("Test Description")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .location("Test Location")
                .maxAttendees(10)
                .owner(user)
                .relatedSkills(skills)
                .type(WEBINAR)
                .status(PLANNED)
                .build();

        eventWithoutSkills = Event.builder()
                .id(eventWithoutSkillsId)
                .title(eventWithoutSkillsTitle)
                .description("Test Description")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .location("Test Location")
                .maxAttendees(10)
                .owner(user)
                .type(WEBINAR)
                .status(PLANNED)
                .build();
    }

    @Test
    void testShouldSuccessfullyCreateEventWithValidData() {
        when(userContext.getUserId()).thenReturn(ownerId);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(skillRepository.findAllById(skillIds)).thenReturn(skills);
        when(eventRepository.save(event)).thenReturn(event);

        eventService.create(event, skillIds);
        ArgumentCaptor<Event> argumentCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository, times(1)).save(argumentCaptor.capture());
        Event capturedEvent = argumentCaptor.getValue();

        assertEquals(eventId, capturedEvent.getId());
        assertEquals(eventTitle, capturedEvent.getTitle());
    }

    @Test
    void testShouldThrowExceptionWhenUserNotFound() {
        when(userContext.getUserId()).thenReturn(ownerId);
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());
        String expectedMessage = String.format(USER_NOT_FOUND, ownerId);

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class,
                () -> eventService.create(event, skillIds));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testShouldThrowExceptionWhenUserDoesNotHaveAllRequiredSkills() {
        when(userContext.getUserId()).thenReturn(userWithoutSkillsId);
        when(userRepository.findById(userWithoutSkillsId)).thenReturn(Optional.of(userWithoutSkills));
        String exceptionMessage = String.format(NOT_ENOUGH_SKILLS, skill2);

        doThrow(new EventCreationNotAllowedException(exceptionMessage))
                .when(eventValidation).validateUserHasAllEventSkills(skillIds, userWithoutSkills);

        EventCreationNotAllowedException exception = assertThrows(EventCreationNotAllowedException.class,
                () -> eventService.create(event, skillIds));

        assertTrue(exception.getMessage().contains(exceptionMessage));
        verify(eventValidation).validateUserHasAllEventSkills(skillIds, userWithoutSkills);
    }

    @Test
    void testShouldCreateEventWhenEventWithEmptySkillsList() {
        when(userContext.getUserId()).thenReturn(userWithoutSkillsId);
        when(userRepository.findById(userWithoutSkillsId)).thenReturn(Optional.of(userWithoutSkills));
        when(skillRepository.findAllById(Collections.emptyList())).thenReturn(Collections.emptyList());
        when(eventRepository.save(eventWithoutSkills)).thenReturn(eventWithoutSkills);

        eventService.create(eventWithoutSkills, Collections.emptyList());
        ArgumentCaptor<Event> argumentCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository, times(1)).save(argumentCaptor.capture());
        Event capturedEvent = argumentCaptor.getValue();

        assertEquals(eventWithoutSkillsId, capturedEvent.getId());
        assertEquals(eventWithoutSkillsTitle, capturedEvent.getTitle());
    }
}
