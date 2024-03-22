package school.faang.user_service.validation.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventValidatorTest {

    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    private EventValidator eventValidator;

    private Event event;
    private EventDto eventDto;
    private Skill requiredSkill;
    private Skill userSkill;
    private User user;

    @BeforeEach
    void setUp() {
        requiredSkill = Skill.builder()
                .id(1)
                .title("Required skill")
                .build();
        userSkill = Skill.builder()
                .id(2)
                .title("User's skill")
                .build();
        user = User.builder()
                .id(3)
                .username("Valid username")
                .skills(List.of(userSkill))
                .build();
        event = Event.builder()
                .id(4)
                .title("Valid event title")
                .description("Valid description")
                .startDate(LocalDateTime.of(2024, Month.JANUARY, 2, 12, 0))
                .endDate(LocalDateTime.now().plusYears(5))
                .location("Valid location")
                .maxAttendees(50)
                .owner(user)
                .relatedSkills(List.of(requiredSkill))
                .build();
        eventDto = EventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .ownerId(event.getOwner().getId())
                .description(event.getDescription())
                .relatedSkillsIds(List.of(requiredSkill.getId()))
                .location(event.getLocation())
                .maxAttendees(event.getMaxAttendees())
                .build();
    }

    @Test
    void validateUserHasRequiredSkills_UserHasRequiredSkills_ShouldNotThrow() {
        long userId = eventDto.getOwnerId();
        when(skillRepository.findAllByUserId(userId)).thenReturn(List.of(requiredSkill));

        eventValidator.validateUserHasRequiredSkills(eventDto);

        assertAll(
                () -> verify(skillRepository, times(1)).findAllByUserId(userId),
                () -> assertDoesNotThrow(() -> eventValidator.validateUserHasRequiredSkills(eventDto))
        );
    }

    @Test
    void validateUserHasRequiredSkills_UserDoesntHaveRequiredSkills_ShouldThrowDataValidationException() {
        when(skillRepository.findAllByUserId(eventDto.getOwnerId())).thenReturn(List.of(userSkill));

        assertThrows(DataValidationException.class,
                () -> eventValidator.validateUserHasRequiredSkills(eventDto));
    }

    @Test
    void validateUserHasRequiredSkills_UserDoesntHaveAnySkills_ShouldThrowDataValidationException() {
        when(skillRepository.findAllByUserId(eventDto.getOwnerId())).thenReturn(Collections.emptyList());

        assertThrows(DataValidationException.class,
                () -> eventValidator.validateUserHasRequiredSkills(eventDto));
    }

    @Test
    void validateUserIsOwnerOfEvent_UserOwnsEvent_ShouldNotThrow() {
        assertDoesNotThrow(() -> eventValidator.validateUserIsOwnerOfEvent(user, eventDto));
    }

    @Test
    void validateUserIsOwnerOfEvent_UserDoesntOwnEvent_ShouldThrowIllegalStateException() {
        eventDto.setOwnerId(19L);

        assertThrows(DataValidationException.class,
                () -> eventValidator.validateUserIsOwnerOfEvent(user, eventDto));
    }
}
