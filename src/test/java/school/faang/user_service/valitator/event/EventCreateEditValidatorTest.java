package school.faang.user_service.valitator.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventCreateEditDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EventCreateEditValidatorTest {

    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    private EventCreateEditValidator validator;

    @Test
    void shouldPassValidation() {
        EventCreateEditDto eventCreateEditDto = getEventCreateEditDto();
        Mockito.doReturn(eventCreateEditDto.getRelatedSkillIds().stream()
                        .map(this::getSkill)
                        .toList())
                .when(skillRepository)
                .findAllByUserId(eventCreateEditDto.getOwnerId());

        assertDoesNotThrow(() -> validator.validate(eventCreateEditDto));
    }

    @Test
    void shouldThrowExceptionIfStartDateIsNull() {
        Event event = getEvent();
        LocalDateTime invalidStartDate = null;
        EventCreateEditDto eventCreateEditDto = new EventCreateEditDto(
                event.getTitle(),
                invalidStartDate,
                event.getEndDate(),
                event.getOwner().getId(),
                event.getDescription(),
                event.getRelatedSkills().stream()
                        .map(Skill::getId)
                        .toList(),
                event.getLocation(),
                event.getMaxAttendees(),
                event.getType(),
                event.getStatus()
        );

        assertAll(
                () -> {
                    var exception = assertThrows(DataValidationException.class, () -> validator.validate(eventCreateEditDto));
                    assertThat(exception.getMessage()).isEqualTo("start-date не может быть пустым");
                }
        );

    }

    @Test
    void shouldThrowExceptionIfMissingRequiredSkill() {
        Event event = getEvent();
        List<Skill> invalidSkills = List.of(getSkill(3L));
        EventCreateEditDto eventCreateEditDto = new EventCreateEditDto(
                event.getTitle(),
                event.getStartDate(),
                event.getEndDate(),
                event.getOwner().getId(),
                event.getDescription(),
                Stream.of(event.getRelatedSkills(), invalidSkills)
                        .flatMap(List::stream)
                        .map(Skill::getId)
                        .toList(),
                event.getLocation(),
                event.getMaxAttendees(),
                event.getType(),
                event.getStatus()
        );
        Mockito.doReturn(getEvent().getRelatedSkills())
                .when(skillRepository)
                .findAllByUserId(eventCreateEditDto.getOwnerId());

        assertAll(
                () -> {
                    var exception = assertThrows(DataValidationException.class, () -> validator.validate(eventCreateEditDto));
                    assertThat(exception.getMessage()).isEqualTo("Не возможно установить skill, id: 3");
                }
        );
    }

    @Test
    void shouldThrowExceptionIfTitleIsEmpty() {
        Event event = getEvent();
        String invalidTitle = "";
        EventCreateEditDto eventCreateEditDto = new EventCreateEditDto(
                invalidTitle,
                event.getStartDate(),
                event.getEndDate(),
                event.getOwner().getId(),
                event.getDescription(),
                event.getRelatedSkills().stream()
                        .map(Skill::getId)
                        .toList(),
                event.getLocation(),
                event.getMaxAttendees(),
                event.getType(),
                event.getStatus()
        );

        assertAll(
                () -> {
                    var exception = assertThrows(DataValidationException.class, () -> validator.validate(eventCreateEditDto));
                    assertThat(exception.getMessage()).isEqualTo("title не может быть пустым");
                }
        );
    }

    private EventCreateEditDto getEventCreateEditDto() {
        Event event = getEvent();
        return new EventCreateEditDto(
                event.getTitle(),
                event.getStartDate(),
                event.getEndDate(),
                event.getOwner().getId(),
                event.getDescription(),
                event.getRelatedSkills().stream()
                        .map(Skill::getId)
                        .toList(),
                event.getLocation(),
                event.getMaxAttendees(),
                event.getType(),
                event.getStatus()
        );
    }

    private Event getEvent() {
        var dateNow = LocalDateTime.now();
        return Event.builder()
                .title("title")
                .startDate(dateNow)
                .endDate(dateNow.plusDays(1))
                .owner(getUser(1L))
                .description("description")
                .relatedSkills(Stream.of(1L, 2L)
                        .map(this::getSkill)
                        .toList())
                .type(EventType.POLL)
                .status(EventStatus.PLANNED)
                .maxAttendees(1)
                .build();
    }

    private User getUser(Long id) {
        return User.builder()
                .id(id)
                .build();
    }

    private Skill getSkill(Long id) {
        return Skill.builder()
                .id(id)
                .build();
    }

}