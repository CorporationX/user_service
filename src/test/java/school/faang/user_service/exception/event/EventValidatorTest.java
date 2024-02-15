package school.faang.user_service.exception.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventValidatorTest {
    @InjectMocks
    private EventValidator eventValidator;

    @Test
    void shouldValidateTitleEvent() {
        EventDto eventDto = new EventDto();
        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () ->
        {
            eventValidator.validate(eventDto);
        });
        assertEquals("Не заполнено название", dataValidationException.getMessage());
    }

    @Test
    void shouldValidateStartDateEvent() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle("Test");
        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () ->
        {
            eventValidator.validate(eventDto);
        });
        assertEquals("Не заполнена дата", dataValidationException.getMessage());
    }

    @Test
    void shouldValidateOwnerIdEvent() {
        LocalDateTime now = LocalDateTime.now();
        EventDto eventDto = new EventDto();
        eventDto.setTitle("Test");
        eventDto.setStartDate(now);
        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () ->
        {
            eventValidator.validate(eventDto);
        });
        assertEquals("Не заполнен пользователь", dataValidationException.getMessage());
    }

    @Test
    void shouldValidateTitleEventUpdate() {
        EventUpdateDto eventUpdateDto = new EventUpdateDto();
        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () ->
        {
            eventValidator.validate(eventUpdateDto);
        });
        assertEquals("Не заполнено название", dataValidationException.getMessage());
    }

    @Test
    void shouldValidateStartDateEventUpdate() {
        EventUpdateDto eventUpdateDto = new EventUpdateDto();
        eventUpdateDto.setTitle("Test");
        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () ->
        {
            eventValidator.validate(eventUpdateDto);
        });
        assertEquals("Не заполнена дата", dataValidationException.getMessage());
    }

    @Test
    void shouldValidateOwnerIdEventUpdate() {
        LocalDateTime now = LocalDateTime.now();
        EventUpdateDto eventUpdateDto = new EventUpdateDto();
        eventUpdateDto.setTitle("Test");
        eventUpdateDto.setStartDate(now);
        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () ->
        {
            eventValidator.validate(eventUpdateDto);
        });
        assertEquals("Не заполнен пользователь", dataValidationException.getMessage());
    }

    @Test
    void shouldValidateSkill() {
        List<String> skillListDto = new ArrayList<>();
        List<Skill> skillListUser = new ArrayList<>();
        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () ->
        {
            eventValidator.validate(skillListDto, skillListUser);
        });
        assertEquals("Нет подходящих скилов", dataValidationException.getMessage());
    }

    @Test
    void shouldValidateSkillOptional() {
        Optional<Event> eventOptional = Optional.empty();
        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () ->
        {
            eventValidator.validate(eventOptional);
        });
        assertEquals("Такого эвента нет", dataValidationException.getMessage());
    }

}