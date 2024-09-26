package school.faang.user_service.validator.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class EventValidatorTest {

    @Mock
    SkillRepository skillRepository;

    @InjectMocks
    private EventValidator eventValidator;

    @Nested
    class PositiveTests {
        @Test
        @DisplayName("if EventDto is valid then return true")
        void testValidateOwnerSkillsWithValidArgs() {
            EventDto eventDto = EventDto.builder()
                    .id(1L)
                    .title("Новое событие")
                    .startDate(LocalDateTime.of(2023, 10, 1, 12, 0))
                    .endDate(LocalDateTime.of(2024, 10, 1, 12, 0))
                    .ownerId(1L)
                    .description("description")
                    .relatedSkills(List.of(1L, 2L))
                    .location("location")
                    .maxAttendees(5)
                    .build();

            Skill skill1 = Skill.builder()
                    .id(1L)
                    .build();

            Skill skill2 = Skill.builder()
                    .id(2L)
                    .build();

            List<Skill> skillsOwner = List.of(skill1, skill2);

            when(skillRepository.findAllByUserId(eventDto.getOwnerId())).thenReturn(skillsOwner);
            assertDoesNotThrow(() -> eventValidator.validateOwnerSkills(eventDto));
        }

        @Test
        @DisplayName("if EventDto is valid then return true")
        void testValidateEventDtoWithValidArgs() {
            EventDto eventDto = EventDto.builder()
                    .id(1L)
                    .title("Новое событие")
                    .startDate(LocalDateTime.of(2023, 10, 1, 12, 0))
                    .endDate(LocalDateTime.of(2024, 10, 1, 12, 0))
                    .ownerId(1L)
                    .description("description")
                    .relatedSkills(List.of(1L, 2L))
                    .location("location")
                    .maxAttendees(5)
                    .build();

            assertDoesNotThrow(() -> eventValidator.validateEventDto(eventDto));
        }
    }

    @Nested
    class NegativeTests {
        @Test
        @DisplayName("if EventDto is not valid then throw DataValidationException")
        void testValidateOwnerSkillsWithInvalidArgs() {
            EventDto eventDto = EventDto.builder()
                    .id(1L)
                    .title("Новое событие")
                    .startDate(LocalDateTime.of(2023, 10, 1, 12, 0))
                    .endDate(LocalDateTime.of(2024, 10, 1, 12, 0))
                    .ownerId(1L)
                    .description("description")
                    .relatedSkills(List.of(1L, 2L))
                    .location("location")
                    .maxAttendees(5)
                    .build();

            Skill skill1 = Skill.builder()
                    .id(1L)
                    .build();

            Skill skill2 = Skill.builder()
                    .id(3L)
                    .build();

            List<Skill> skillsOwner = List.of(skill1, skill2);

            when(skillRepository.findAllByUserId(eventDto.getOwnerId())).thenReturn(skillsOwner);

            assertThrows(DataValidationException.class, () -> eventValidator.validateOwnerSkills(eventDto));

        }

        @Test
        @DisplayName("if EventDto is valid then return true")
        void testValidateEventDtoWithInvalidArgs() {
            EventDto eventDto = EventDto.builder()
                    .id(null)
                    .title("")
                    .startDate(null)
                    .ownerId(null)
                    .relatedSkills(null)
                    .build();

            assertThrows(DataValidationException.class, () -> eventValidator.validateEventDto(eventDto));
        }
    }
}