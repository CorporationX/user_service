package school.faang.user_service.validator.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.event.DataValidationException;
import school.faang.user_service.exceptions.event.NotFoundException;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventValidatorImplTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private EventValidatorImpl validator;

    private final Long id = 1L;
    private EventDto eventDto;

    @BeforeEach
    void init() {
        eventDto = EventDto.builder()
                .id(1L)
                .ownerId(1L)
                .title("eventDto")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();
    }

    @Test
    void validateNullTitleEvent() {
        eventDto.setTitle(null);

        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validate(eventDto));
        assertEquals("title can't be null", e.getMessage());
    }

    @Test
    void validateBlancTitleEvent() {
        eventDto.setTitle("");

        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validate(eventDto));
        assertEquals("title can't be blank", e.getMessage());
    }

    @Test
    void validateNullStartDateEvent() {
        eventDto.setStartDate(null);

        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validate(eventDto));
        assertEquals("start date can't be null", e.getMessage());
    }

    @Test
    void validateNoTitleEvent() {
        eventDto.setOwnerId(null);

        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validate(eventDto));
        assertEquals("event owner can't be null", e.getMessage());
    }

    @Test
    void validateGoodEvent() {
        assertDoesNotThrow(() -> validator.validate(eventDto));
    }

    @Test
    void validateOwnersRequiredSkillsNotFoundUser() {
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> validator.validateOwnersRequiredSkills(eventDto));
        assertEquals("user with id=" + id + " not found", e.getMessage());
    }

    @Test
    void validateOwnersRequiredSkillsNoRequiredSkills() {
        List<Skill> skills = List.of(
                Skill.builder().id(1L).build()
        );
        List<SkillDto> skillDtoList = List.of(
                SkillDto.builder().id(1L).build(),
                SkillDto.builder().id(2L).build()
        );
        User user = User.builder()
                .id(id)
                .skills(skills)
                .build();
        eventDto.setRelatedSkills(skillDtoList);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validateOwnersRequiredSkills(eventDto));
        assertEquals("user with id=" + id + " has no enough skills to create event", e.getMessage());
    }

    @Test
    void validateOwnersRequiredSkillsGoodEventAndUser() {
        List<Skill> skills = List.of(
                Skill.builder().id(1L).build(),
                Skill.builder().id(2L).build()
        );
        List<SkillDto> skillDtoList = List.of(
                SkillDto.builder().id(1L).build(),
                SkillDto.builder().id(2L).build()
        );
        User user = User.builder()
                .id(id)
                .skills(skills)
                .build();
        eventDto.setRelatedSkills(skillDtoList);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

       assertDoesNotThrow(() -> validator.validateOwnersRequiredSkills(eventDto));
    }
}