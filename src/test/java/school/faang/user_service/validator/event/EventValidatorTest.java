package school.faang.user_service.validator.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exceptions.event.DataValidationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EventValidatorTest {
    @Spy
    EventValidator validator;
    EventDto event;

    @BeforeEach
    void setEvent() {
        List< SkillDto > skillDtoList = List.of(
                new SkillDto(1L, "skill1"),
                new SkillDto(2L, "skill2")
        );
        event = EventDto.builder()
                .ownerId(1L)
                .title("dto")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1L))
                .relatedSkills(skillDtoList)
                .build();
    }

    @Test
    void validateNullTitleEvent() {
        event.setTitle(null);
        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validate(event));
        assertEquals("title can't be null - " + event, e.getMessage());
    }

    @Test
    void validateBlankTitleEvent() {
        event.setTitle("");
        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validate(event));
        assertEquals("title can't be blank - " + event, e.getMessage());
    }

    @Test
    void validateNullStartDateEvent() {
        event.setStartDate(null);
        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validate(event));
        assertEquals("start date can't be null - " + event, e.getMessage());
    }

    @Test
    void validateNullOwnerIdEvent() {
        event.setOwnerId(null);
        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validate(event));
        assertEquals("event owner can't be null - " + event, e.getMessage());
    }
}