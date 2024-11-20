package school.faang.user_service.service.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventDtoValidatorTest {
    @InjectMocks
    private EventDtoValidator validator;
    @Mock
    private UserService userService;
    @Mock
    private SkillMapper skillMapper;

    private final Long testId = 1L;
    private final String testTitle = "testTitle";


    @Test
    public void testNullTitle() {
        Assertions.assertThrows(
                DataValidationException.class,
                () -> validator.validateTitle(null)
        );
    }

    @Test
    public void testBlankTitle() {
        Assertions.assertThrows(
                DataValidationException.class,
                () -> validator.validateTitle(" ")
        );
    }

    @Test
    public void testValidTitle() {
        Assertions.assertDoesNotThrow(() -> validator.validateTitle(testTitle));
    }

    @Test
    public void testNullStartDate() {
        Assertions.assertThrows(
                DataValidationException.class,
                () -> validator.validateStartDate(null)
        );
    }

    @Test
    public void testPastStartDate() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        Assertions.assertThrows(
                DataValidationException.class,
                () -> validator.validateStartDate(yesterday)
        );
    }

    @Test
    public void testValidStartDate() {
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        Assertions.assertDoesNotThrow(() -> validator.validateStartDate(tomorrow));
    }

    @Test
    public void testIncompleteSkillsOfOwner() {
        EventDto eventDto = getEventDto();
        User user = getUser();

        when(userService.findById(eventDto.getOwnerId())).thenReturn(user);
        when(skillMapper.toListEntity(eventDto.getRelatedSkills()))
                .thenReturn(List.of(Skill.builder().id(testId + 1).build()));

        Assertions.assertThrows(
                DataValidationException.class,
                () -> validator.validateOwnerOfEvent(eventDto)
        );
    }

    @Test
    public void testValidSkillsOfOwner() {
        EventDto eventDto = getEventDto();
        User user = getUser();

        when(userService.findById(eventDto.getOwnerId())).thenReturn(user);
        when(skillMapper.toListEntity(eventDto.getRelatedSkills()))
                .thenReturn(List.of(Skill.builder().id(testId).build()));

        Assertions.assertDoesNotThrow(() -> validator.validateOwnerOfEvent(eventDto));
    }

    private List<SkillDto> getSkillDtoList() {
        return List.of(new SkillDto());
    }

    private List<Skill> getSkillList() {
        return List.of(Skill.builder().id(testId).build());
    }

    private EventDto getEventDto() {
        return EventDto.builder()
                .ownerId(testId)
                .relatedSkills(getSkillDtoList())
                .build();
    }

    private User getUser() {
        return User.builder()
                .skills(getSkillList())
                .build();
    }
}