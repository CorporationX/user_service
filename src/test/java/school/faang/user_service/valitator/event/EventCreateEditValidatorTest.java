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
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.valitator.Error;
import school.faang.user_service.valitator.ValidationResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

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


        ValidationResult actualResult = validator.validate(eventCreateEditDto);

        assertFalse(actualResult.hasErrors());
    }

    @Test
    void invalid() {
        EventCreateEditDto eventCreateEditDto = getInvalidEventCreateEditDto();
        Mockito.doReturn(getEvent().getRelatedSkills())
                .when(skillRepository)
                .findAllByUserId(eventCreateEditDto.getOwnerId());

        ValidationResult actualResult = validator.validate(eventCreateEditDto);

        assertThat(actualResult.getErrors()).hasSize(4);
        assertThat(actualResult.getErrors()).contains(
                Error.of("invalid.start-date", "start-date не может быть пустым"),
                Error.of("invalid.title", "title не может быть пустым"),
                Error.of("invalid.related-skill", "Не возможно установить skill, id: 3"),
                Error.of("invalid.related-skill", "Не возможно установить skill, id: 4"));
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

    private EventCreateEditDto getInvalidEventCreateEditDto() {
        Event event = getEvent();
        String invalidTitele = "";
        LocalDateTime invalidStartDate = null;
        List<Skill> invalidSkills = List.of(getSkill(3L), getSkill(4L));
        return new EventCreateEditDto(
                invalidTitele,
                invalidStartDate,
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