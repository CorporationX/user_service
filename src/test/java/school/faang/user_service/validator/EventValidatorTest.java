package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventValidatorTest {

    private EventValidator eventValidator;
    private User user;
    private Event event;

    @BeforeEach
    public void setUp() {
        eventValidator = new EventValidator();
    }

    @Test
    public void testCheckUserIsOwnerEvent() {
        user = User.builder().id(1L).build();
        event = Event.builder()
                .owner(User.builder().id(2L).build())
                .build();

        assertThrows(DataValidationException.class, () -> eventValidator.checkUserIsOwnerEvent(user, event));
    }

    @Test
    public void testCheckNeedSkillsForEvent() {
        user = User.builder().skills(List.of(
                Skill.builder().title("Skill1").build(),
                Skill.builder().title("Skill2").build(),
                Skill.builder().title("Skill3").build()
        )).build();

        event = Event.builder().relatedSkills(List.of(
                Skill.builder().title("Skill5").build(),
                Skill.builder().title("Skill8").build(),
                Skill.builder().title("Skill6").build()
        )).build();

        assertThrows(DataValidationException.class, () -> eventValidator.checkNeedSkillsForEvent(user, event));
    }
}