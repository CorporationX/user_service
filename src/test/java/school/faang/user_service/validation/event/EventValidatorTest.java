package school.faang.user_service.validation.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventValidatorTest {

    @Mock
    private SkillRepository skillRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private SkillMapper skillMapper;
    @InjectMocks
    private EventValidator eventValidator;

    private Event event;
    private EventDto eventDto;
    private Skill requiredSkill;
    private SkillDto requiredSkillDto;
    private Skill userSkill;
    private SkillDto userSkillDto;
    private User user;

    @BeforeEach
    void setUp() {
        requiredSkill = Skill.builder()
                .id(1)
                .title("Required skill")
                .build();
        requiredSkillDto = SkillDto.builder()
                .id(requiredSkill.getId())
                .title(requiredSkill.getTitle())
                .build();
        userSkill = Skill.builder()
                .id(2)
                .title("User's skill")
                .build();
        userSkillDto = SkillDto.builder()
                .id(userSkill.getId())
                .title(userSkill.getTitle())
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
                .relatedSkills(List.of(requiredSkillDto))
                .location(event.getLocation())
                .maxAttendees(event.getMaxAttendees())
                .build();
    }

    @Test
    void validateEventDtoFields_ValidEventFields_ShouldNotThrow() {
        assertDoesNotThrow(() ->
                eventValidator.validateEventDtoFields(eventDto));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void validateEventDtoFields_InvalidTitle_ShouldThrowDataValidationException(String title) {
        eventDto.setTitle(title);

        assertThrows(DataValidationException.class, () ->
                eventValidator.validateEventDtoFields(eventDto));
    }

    @Test
    void validateEventDtoFields_NullStartDate_ShouldThrowDataValidationException() {
        eventDto.setStartDate(null);

        assertThrows(DataValidationException.class, () ->
                eventValidator.validateEventDtoFields(eventDto));
    }

    @Test
    void validateEventDtoFields_NullOwnerId_ShouldThrowDataValidationException() {
        eventDto.setOwnerId(null);

        assertThrows(DataValidationException.class, () ->
                eventValidator.validateEventDtoFields(eventDto));
    }

    @Test
    void validateUserHasRequiredSkills_UserHasRequiredSkills_ShouldNotThrow() {
        when(skillRepository.findAllByUserId(anyLong())).thenReturn(List.of(requiredSkill));

        assertDoesNotThrow(() ->
                eventValidator.validateUserHasRequiredSkills(eventDto));
    }

    @Test
    void validateUserHasRequiredSkills_UserDoesntHaveRequiredSkills_ShouldThrowDataValidationException() {
        when(skillRepository.findAllByUserId(anyLong())).thenReturn(List.of(userSkill));
        when(skillMapper.toEntity(eventDto.getRelatedSkills())).thenReturn(List.of((requiredSkill)));

        assertThrows(DataValidationException.class, () ->
                eventValidator.validateUserHasRequiredSkills(eventDto));
    }

    @Test
    void validateUserHasRequiredSkills_UserDoesntHaveAnySkills_ShouldThrowDataValidationException() {
        when(skillRepository.findAllByUserId(anyLong())).thenReturn(null);

        assertThrows(DataValidationException.class, () ->
                eventValidator.validateUserHasRequiredSkills(eventDto));
    }

    @Test
    void validateUserIsOwnerOfEvent_UserOwnsEvent_ShouldNotThrow() {
        assertDoesNotThrow(() ->
                eventValidator.validateUserIsOwnerOfEvent(user, eventDto));
    }

    @Test
    void validateUserIsOwnerOfEvent_UserDoesntOwnEvent_ShouldThrowIllegalStateException() {
        eventDto.setOwnerId(19L);

        assertThrows(DataValidationException.class, () ->
                eventValidator.validateUserIsOwnerOfEvent(user, eventDto));
    }

    @Test
    void validateEventExistsById_EventExists_ShouldNotThrow() {
        when(eventRepository.existsById(anyLong())).thenReturn(true);

        assertDoesNotThrow(() ->
                eventValidator.validateEventExistsById(10L));
    }

    @Test
    void validateEventExistsById_EventDoesntExist_ShouldThrowNoSuchElementException() {
        when(eventRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(DataValidationException.class, () ->
                eventValidator.validateEventExistsById(10L));
    }
}
