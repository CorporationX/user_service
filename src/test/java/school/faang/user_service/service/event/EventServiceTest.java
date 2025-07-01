package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.config.redis.RedisTtlProperties;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.promotion.PromotionStatus;
import school.faang.user_service.entity.promotion.PromotionType;
import school.faang.user_service.entity.skill.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.event.ActivePromotionExistsException;
import school.faang.user_service.exception.event.EventNotFoundException;
import school.faang.user_service.exception.event.EventValidationException;
import school.faang.user_service.repository.event.EventFilterRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.promotion.PromotionRedisService;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.event.EventValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @InjectMocks
    private EventService eventService;
    @Mock
    private UserService userService;
    @Mock
    private SkillService skillService;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventFilterRepository eventFilterRepository;
    @Mock
    private EventValidator eventValidator;
    @Mock
    private UserContext userContext;
    @Mock
    private PromotionRedisService promotionRedisService;
    @Mock
    private RedisTtlProperties redisTtlProperties;
    @Mock
    private EventRedisService eventRedisService;
    @Mock
    private ApplicationContext applicationContext;

    private Event event;
    private User user;
    private List<Long> skillIds;
    private List<Skill> skills;

    @BeforeEach
    void setUp() {
        event = new Event();
        user = new User();
        skillIds = List.of(10L, 20L);
        skills = skillIds.stream()
                .map(id -> {
                    Skill s = new Skill();
                    s.setId(id);
                    return s;
                }).toList();
    }

    @Test
    void createEvent_withValidSkills_shouldSucceed() {
        long userId = 1L;
        user = new User();
        user.setId(userId);

        event = new Event();

        when(userContext.getUserId()).thenReturn(userId);
        when(userService.getUserByIdOrThrow(userId)).thenReturn(user);
        when(skillService.getSkillsByIds(skillIds)).thenReturn(skills);
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        Event result = eventService.createEvent(event, skillIds);

        assertEquals(user, result.getOwner());
        assertEquals(skills, result.getRelatedSkills());
        verify(eventValidator).validateOwnerHasSkills(userId, skills);
        verify(eventValidator).validateEventDates(event.getStartDate(), event.getEndDate());
        verify(eventRepository).save(event);
    }

    @Test
    void getEvent_existingId_shouldReturnEvent() {
        long eventId = 1L;
        event = new Event();
        event.setId(eventId);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        Event actual = eventService.getEventById(eventId);

        assertSame(event, actual);
    }

    @Test
    void getEvent_nonexistentId_shouldThrow() {
        long eventId = 1L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventService.getEventById(eventId));
    }

    @Test
    void deleteEvent_successfully() {
        long eventId = 1L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        eventService.deleteEventById(eventId);

        verify(eventRepository).deleteById(eventId);
    }

    @Test
    void testDeleteEvent_eventNotFound() {
        long eventId = 1L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventService.deleteEventById(eventId));

        verify(eventRepository, never()).deleteById(eventId);
    }

    @Test
    void testDeleteEvent_ExistsActivePromotion() {
        long eventId = 1L;
        Promotion promotion = new Promotion();
        promotion.setId(18L);
        promotion.setType(PromotionType.EVENT);
        promotion.setStatus(PromotionStatus.ACTIVE);
        event.setPromotions(List.of(promotion));

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        assertThrows(ActivePromotionExistsException.class, () -> eventService.deleteEventById(eventId));

        verify(eventRepository, never()).deleteById(eventId);
    }

    @Test
    void updateEvent_valid_shouldUpdateSkills() {
        long userId = 1L;
        user = new User();
        user.setId(userId);

        event = new Event();
        event.setOwner(user);
        event.setRelatedSkills(new ArrayList<>());

        when(userContext.getUserId()).thenReturn(userId);
        when(skillService.getSkillsByIds(skillIds)).thenReturn(skills);
        when(eventRepository.save(event)).thenReturn(event);

        Event result = eventService.updateEventData(event, skillIds);

        assertEquals(skills, result.getRelatedSkills());
        verify(eventValidator).validateOwnerHasSkills(userId, skills);
        verify(eventValidator).validateEventDates(event.getStartDate(), event.getEndDate());
        verify(eventRedisService).updatePromotedEvent(event);
    }

    @Test
    void updateEvent_otherUser_shouldThrow() {
        long eventId = 1L;
        event = new Event();
        event.setId(eventId);
        event.setOwner(user);
        when(userContext.getUserId()).thenReturn(eventId);

        assertThrows(EventValidationException.class, () -> eventService.updateEventData(event, null));
    }

    @Test
    void getOwnedEvents_shouldReturnList() {
        long eventId = 1L;
        event = new Event();
        event.setId(eventId);

        List<Event> expected = List.of(event);
        when(eventRepository.findAllByUserId(eventId)).thenReturn(expected);

        assertEquals(expected, eventService.getOwnedEvents(eventId));
    }

    @Test
    void getParticipatedEvents_shouldReturnList() {
        long eventId = 1L;
        event = new Event();
        event.setId(eventId);

        List<Event> expected = List.of(event);
        when(eventRepository.findParticipatedEventsByUserId(eventId)).thenReturn(expected);

        assertEquals(expected, eventService.getParticipatedEvents(eventId));
    }

    @Test
    void getAllEvents_shouldReturnAll() {
        long eventId = 1L;
        event = new Event();
        event.setId(eventId);

        List<Event> expected = List.of(event);
        when(eventRepository.findAll()).thenReturn(expected);

        assertEquals(expected, eventService.getAllEvents());
    }
}
