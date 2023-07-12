package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EventValidatorTest {
    EventDto eventDto;
    List<Long> skillIds;
    @Mock
    private UserService userService;
    @InjectMocks
    private EventValidator eventValidator;

    @BeforeEach
    public void init() {
        eventDto = new EventDto(4L, "fdgdfg", LocalDateTime.now(), LocalDateTime.now(),
                0L, "hfgh", new ArrayList<>(), "location", 1);

        skillIds = eventDto.getRelatedSkills().stream()
                .map(SkillDto::getId)
                .toList();
        Mockito.when(userService.areOwnedSkills(eventDto.getOwnerId(), skillIds))
                .thenReturn(false);
    }

    @Test
    public void testThrowDataValidationException() {
        assertThrows(DataValidationException.class, () -> {
            eventValidator.checkIfUserHasSkillsRequired(eventDto);
        });
    }
}