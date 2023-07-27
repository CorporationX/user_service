package school.faang.user_service.validate.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.event.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EventValidateTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventValidate eventValidate;

    @Test
    public void testValidateEvent_ValidEventDto() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle("Test Event");
        eventDto.setStartDate(LocalDateTime.now());
        eventDto.setOwnerId(1L);

        eventValidate.validateEvent(eventDto);
    }

    @Test
    public void testValidateEvent_InvalidTitle() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle("");

        assertThrows(DataValidationException.class, () -> eventValidate.validateEvent(eventDto));
    }

    @Test
    public void testValidateEvent_NullStartDate() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle("Test Event");

        assertThrows(DataValidationException.class, () -> eventValidate.validateEvent(eventDto));
    }

    @Test
    public void testValidateEvent_InvalidOwnerId() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle("Test Event");
        eventDto.setStartDate(LocalDateTime.now());
        eventDto.setOwnerId(-1L);

        assertThrows(DataValidationException.class, () -> eventValidate.validateEvent(eventDto));
    }

    @Test
    public void testCheckThatUserHasNecessarySkills_ValidSkills() {
        EventDto eventDto = EventDto.builder()
                .ownerId(1L)
                .skillIds(List.of(1L, 2L, 3L))
                .build();

        Skill skill1 = Skill.builder().id(1L).build();
        Skill skill2 = Skill.builder().id(2L).build();
        Skill skill3 = Skill.builder().id(3L).build();
        User user = new User();
        user.setId(1L);
        user.setSkills(Arrays.asList(skill1, skill2, skill3));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        eventValidate.checkThatUserHasNecessarySkills(eventDto);
    }

    @Test
    public void testCheckThatUserHasNecessarySkills_InsufficientSkills() {
        EventDto eventDto = EventDto.builder()
                .ownerId(1L)
                .skillIds(List.of(1L, 2L, 3L))
                .build();

        Skill skill1 = Skill.builder().id(2L).build();
        Skill skill2 = Skill.builder().id(3L).build();
        User user = new User();
        user.setId(1L);
        user.setSkills(Arrays.asList(skill1, skill2));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThrows(DataValidationException.class, () -> eventValidate.checkThatUserHasNecessarySkills(eventDto));
    }
}