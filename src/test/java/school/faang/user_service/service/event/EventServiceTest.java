package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class EventServiceTest {
    @Mock
    private UserRepository userRepository;

    private final EventService eventService;

    @Autowired
    EventServiceTest(EventService eventService) {
        this.eventService = eventService;
    }

    @Test
    public void testValidateUserSkills_UserHasAllSkills() {
        SkillDto skill1 = new SkillDto(1L, "Skill 1");
        SkillDto skill2 = new SkillDto(2L, "Skill 2");
        SkillDto skill3 = new SkillDto(3L, "Skill 3");

        List<SkillDto> requiredSkills = Arrays.asList(skill1, skill2, skill3);
        Long ownerId = 1L;

        User owner = new User();
        owner.setId(ownerId);
        owner.setSkills(Arrays.asList(
                new Skill(1L, "Skill 1"),
                new Skill(2L, "Skill 2"),
                new Skill(3L, "Skill 3")
        ));

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        EventDto event = new EventDto();
        event.setRelatedSkills(requiredSkills);
        event.setOwnerId(ownerId);

        assertDoesNotThrow(() -> eventService.validateUserSkills(event));
    }

    @Test
    public void testValidateUserSkills_UserMissingSkills() {
        SkillDto skill1 = new SkillDto(1L, "Skill 1");
        SkillDto skill2 = new SkillDto(2L, "Skill 2");
        SkillDto skill3 = new SkillDto(3L, "Skill 3");

        List<SkillDto> requiredSkills = Arrays.asList(skill1, skill2, skill3);
        Long ownerId = 1L;

        User owner = new User();
        owner.setId(ownerId);
        owner.setSkills(Arrays.asList(
                new Skill(1L, "Skill 1"),
                new Skill(2L, "Skill 2")
        ));

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        EventDto event = new EventDto();
        event.setRelatedSkills(requiredSkills);
        event.setOwnerId(ownerId);

        assertThrows(DataValidationException.class, () -> eventService.validateUserSkills(event));
    }

}