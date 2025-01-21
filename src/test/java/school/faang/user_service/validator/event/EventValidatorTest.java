package school.faang.user_service.validator.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.adapter.user.UserRepositoryAdapter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventValidatorTest {

    @InjectMocks
    private EventValidator eventValidator;

    @Mock
    private UserRepositoryAdapter userService;

    @Captor
    private ArgumentCaptor<Long> longCaptor;

    private static final long OWNER_ID = 1L;
    private static final List<Long> RELATED_SKILL_IDS = List.of(1L, 2L);
    private static final Skill FIRST_SKILL = Skill.builder().id(1L).build();
    private static final Skill SECOND_SKILL = Skill.builder().id(2L).build();

    @Test
    void testEventCheck_TitleIsNull_ThrowsException() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle(null);
        Exception exception = assertThrows(DataValidationException.class,
                () -> eventValidator.validateEvent(eventDto));
        assertEquals("Event title не может быть пустым", exception.getMessage());
    }

    @Test
    void testEventCheck_TitleIsBlank_ThrowsException() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle("  ");
        Exception exception = assertThrows(DataValidationException.class,
                () -> eventValidator.validateEvent(eventDto));
        assertEquals("Event title не может быть пустым", exception.getMessage());
    }

    @Test
    void testEventCheck_TitleIsTooLong_ThrowsException() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle("A".repeat(65));
        Exception exception = assertThrows(DataValidationException.class,
                () -> eventValidator.validateEvent(eventDto));
        assertEquals("Длина Event title не может быть больше 64", exception.getMessage());
    }

    @Test
    void testEventCheck_DescriptionIsNull_ThrowsException() {
        EventDto eventDto = new EventDto();
        eventDto.setDescription(null);
        Exception exception = assertThrows(DataValidationException.class,
                () -> eventValidator.validateEvent(eventDto));
        assertEquals("Event description не может быть пустым", exception.getMessage());
    }

    @Test
    void testEventCheck_DescriptionIsBlank_ThrowsException() {
        EventDto eventDto = new EventDto();
        eventDto.setDescription("  ");
        Exception exception = assertThrows(DataValidationException.class,
                () -> eventValidator.validateEvent(eventDto));
        assertEquals("Event description не может быть пустым", exception.getMessage());
    }

    @Test
    void testEventCheck_DescriptionIsTooLong_ThrowsException() {
        EventDto eventDto = new EventDto();
        eventDto.setDescription("A".repeat(4096));
        Exception exception = assertThrows(DataValidationException.class,
                () -> eventValidator.validateEvent(eventDto));
        assertEquals("Длина Event description не может быть больше 4096", exception.getMessage());
    }

    @Test
    void testEventCheck_StartDateIsNull_ThrowsException() {
        EventDto eventDto = new EventDto();
        eventDto.setStartDate(null);
        Exception exception = assertThrows(DataValidationException.class,
                () -> eventValidator.validateEvent(eventDto));
        assertEquals("StartDate не может быть пустым", exception.getMessage());
    }

    @Test
    void testEventCheck_EndDateIsNull_ThrowsException() {
        EventDto eventDto = new EventDto();
        eventDto.setEndDate(null);
        Exception exception = assertThrows(DataValidationException.class,
                () -> eventValidator.validateEvent(eventDto));
        assertEquals("EndDate не может быть пустым", exception.getMessage());
    }

    @Test
    void testEventCheck_LoactionIsNull_ThrowsException() {
        EventDto eventDto = new EventDto();
        eventDto.setLocation(null);
        Exception exception = assertThrows(DataValidationException.class,
                () -> eventValidator.validateEvent(eventDto));
        assertEquals("Event Location не может быть пустым", exception.getMessage());
    }

    @Test
    void testEventCheck_LoactionIsBlank_ThrowsException() {
        EventDto eventDto = new EventDto();
        eventDto.setLocation("  ");
        Exception exception = assertThrows(DataValidationException.class,
                () -> eventValidator.validateEvent(eventDto));
        assertEquals("Event Location не может быть пустым", exception.getMessage());
    }

    @Test
    void testEventCheck_LoactionIsTooLong_ThrowsException() {
        EventDto eventDto = new EventDto();
        eventDto.setLocation("A".repeat(128));
        Exception exception = assertThrows(DataValidationException.class,
                () -> eventValidator.validateEvent(eventDto));
        assertEquals("Длина Event Location не может быть больше 128", exception.getMessage());
    }

    @Test
    void testEventCheck_OwnerIdIsNull_ThrowsException() {
        EventDto eventDto = new EventDto();
        eventDto.setOwnerId(null);
        Exception exception = assertThrows(DataValidationException.class,
                () -> eventValidator.validateEvent(eventDto));
        assertEquals("OwnerId не может быть пустым", exception.getMessage());
    }

    @Test
    void testEventCheck_EventTypeIsNull_ThrowsException() {
        EventDto eventDto = new EventDto();
        eventDto.setEventType(null);
        Exception exception = assertThrows(DataValidationException.class,
                () -> eventValidator.validateEvent(eventDto));
        assertEquals("EventType не может быть пустым", exception.getMessage());
    }

    @Test
    void testEventCheck_EventStatusIsNull_ThrowsException() {
        EventDto eventDto = new EventDto();
        eventDto.setEventStatus(null);
        Exception exception = assertThrows(DataValidationException.class,
                () -> eventValidator.validateEvent(eventDto));
        assertEquals("EventStatus не может быть пустым", exception.getMessage());
    }

    @Test
    void testUserHasSkills_UserHasAllSkills() {
        User user = new User();
        user.setSkills(List.of(FIRST_SKILL, SECOND_SKILL));
        when(userService.getUserById(OWNER_ID)).thenReturn(user);
        boolean result = eventValidator.userHasSkills(OWNER_ID, RELATED_SKILL_IDS);
        assertTrue(result);
        verify(userService, times(1)).getUserById(longCaptor.capture());
        assertEquals(OWNER_ID, longCaptor.getValue());
    }

    @Test
    void testUserHasSkills_UserDoesNotHaveAllSkills() {
        User user = new User();
        user.setSkills(List.of(FIRST_SKILL));
        when(userService.getUserById(OWNER_ID)).thenReturn(user);
        boolean result = eventValidator.userHasSkills(OWNER_ID, RELATED_SKILL_IDS);
        assertFalse(result);
        verify(userService, times(1)).getUserById(longCaptor.capture());
        assertEquals(OWNER_ID, longCaptor.getValue());
    }

    @Test
    void testUserCanCreateEventBySkills_UserCanCreateEvent() {
        User user = new User();
        user.setSkills(List.of(FIRST_SKILL, SECOND_SKILL));
        when(userService.getUserById(OWNER_ID)).thenReturn(user);
        assertDoesNotThrow(() -> eventValidator.userCanCreateEventBySkills(OWNER_ID, RELATED_SKILL_IDS));
        verify(userService, times(1)).getUserById(longCaptor.capture());
        assertEquals(OWNER_ID, longCaptor.getValue());
    }

    @Test
    void testUserCanCreateEventBySkills_UserCannotCreateEvent() {
        User user = new User();
        user.setSkills(List.of(FIRST_SKILL));
        when(userService.getUserById(OWNER_ID)).thenReturn(user);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> eventValidator.userCanCreateEventBySkills(OWNER_ID, RELATED_SKILL_IDS));
        assertEquals("Пользователь не может провести такое событие с такими навыками", exception.getMessage());
        verify(userService, times(1)).getUserById(longCaptor.capture());
        assertEquals(OWNER_ID, longCaptor.getValue());
    }
}
