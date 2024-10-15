package school.faang.user_service.validator.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.model.event.EventDto;
import school.faang.user_service.model.dto.skill.SkillDto;
import school.faang.user_service.model.entity.Skill;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class EventValidatorTest {
    @InjectMocks
    private EventValidator eventValidator;

    @Mock
    private UserRepository userRepository;

    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        eventDto = EventDto.builder()
                .ownerId(1L)
                .startDate(null)
                .relatedSkills(Collections.emptyList())
                .build();
    }

    @Test
    void validateStartDate_whenStartDateIsNull_throwsException() {
        // Arrange
        eventDto = EventDto.builder().startDate(null).build();

        // Act & Assert
        assertThrows(DataValidationException.class, () -> eventValidator.validateStartDate(eventDto));
    }

    @Test
    void validateStartDate_whenStartDateIsNotNull_doesNotThrowException() {
        // Arrange
        eventDto = EventDto.builder().startDate(java.time.LocalDateTime.now()).build();

        // Act & Assert
        eventValidator.validateStartDate(eventDto);
    }

    @Test
    void validateOwnerSkills_whenOwnerDoesNotExist_throwsException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataValidationException.class, () -> eventValidator.validateOwnerSkills(eventDto));
    }

    @Test
    void validateOwnerSkills_whenOwnerHasNoSkills_throwsException() {
        // Arrange
        eventDto = EventDto.builder()
                .ownerId(1L)
                .relatedSkills(List.of(new SkillDto(3L, "Sth")))
                .build();

        User owner = User.builder().skills(Collections.emptyList()).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        // Act & Assert
        assertThrows(DataValidationException.class, () -> eventValidator.validateOwnerSkills(eventDto));
    }

    @Test
    void validateOwnerSkills_whenEventSkillsDoNotMatchOwnerSkills_throwsException() {
        // Arrange
        Skill skill1 = Skill.builder().id(1L).build();
        User owner = User.builder().skills(List.of(skill1)).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        SkillDto skillDto = SkillDto.builder().id(2L).build();
        eventDto = EventDto.builder()
                .ownerId(1L)
                .relatedSkills(List.of(skillDto))
                .build();

        // Act & Assert
        assertThrows(DataValidationException.class, () -> eventValidator.validateOwnerSkills(eventDto));
    }

    @Test
    void validateOwnerSkills_whenEventSkillsMatchOwnerSkills_doesNotThrowException() {
        // Arrange
        Skill skill1 = Skill.builder().id(1L).build();
        User owner = User.builder().skills(List.of(skill1)).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        SkillDto skillDto = SkillDto.builder().id(1L).build();
        eventDto = EventDto.builder()
                .ownerId(1L)
                .relatedSkills(List.of(skillDto))
                .build();

        // Act & Assert
        eventValidator.validateOwnerSkills(eventDto);
    }

    @Test
    void validateOwnerPresent_whenOwnerDoesNotExist_throwsException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataValidationException.class, () -> eventValidator.validateOwnerPresent(eventDto));
    }

    @Test
    void validateOwnerPresent_whenOwnerExists_doesNotThrowException() {
        // Arrange
        User owner = User.builder().id(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        // Act & Assert
        eventValidator.validateOwnerPresent(eventDto);
    }

    @Test
    void validateEvent_whenTitleIsNull_doesNotThrowsException() {
        // Arrange
        EventDto eventDto = EventDto.builder().title("title").startDate(LocalDateTime.now()).ownerId(1L).build();
        User owner = User.builder().id(1L).build();

        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(owner));

        // Act & Assert
        eventValidator.validateEvent(eventDto);
    }
}
