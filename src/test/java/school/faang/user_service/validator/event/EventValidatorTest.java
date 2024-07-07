package school.faang.user_service.validator.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventValidatorTest {

    private EventValidator eventValidator;

    @BeforeEach
    void setUp() {
        this.eventValidator = new EventValidator();
    }

    @Test
    public void testValidateId(){
        Long negativeId = -1L;
        Long validIdZero = 0L;
        Long validIdPositive = 1L;

        assertFalse(eventValidator.validateId(negativeId));
        assertTrue(eventValidator.validateId(validIdZero));
        assertTrue(eventValidator.validateId(validIdPositive));
    }

    @Test
    public void testValidateBlankTitle() {
        EventDto eventDto = createValidEventDto();
        eventDto.setTitle(" ");

        assertFalse(eventValidator.validateEventDto(eventDto), "Пустой заголовок в EventDto");
    }

    @Test
    public void testValidateNullDate() {
        EventDto eventDto = createValidEventDto();
        eventDto.setStartDate(null);

        assertFalse(eventValidator.validateEventDto(eventDto), "Отсутствует дата в EventDto");
    }

    @Test
    public void testValidateNullOwnerId() {
        EventDto eventDto = createValidEventDto();
        eventDto.setOwnerId(null);

        assertFalse(eventValidator.validateEventDto(eventDto), "Отсутствует пользователь в EventDto");
    }

    @Test
    public void testSuccessfulValidation() {
        EventDto eventDto = createValidEventDto();

        assertTrue(eventValidator.validateEventDto(eventDto), "Валидные параметры EventDto не прошли проверку");
    }

    @Test
    public void checkExistenceSkillNotExist() {
        // Нет навыков
        Map<String, List<Skill>> mapSkills = getSkill(Option.NOT_EXIST);
        User user = new User();
        user.setSkills(mapSkills.get("userSkills"));
        List<Skill> skills = mapSkills.get("skills");
        assertFalse(eventValidator.checkExistenceSkill(user, skills));
    }

    @Test
    public void checkExistenceSkillExistAll() {
        // Все навыки совпадают
        Map<String, List<Skill>> mapSkills = getSkill(Option.EXIST_ALL);
        User user = new User();
        user.setSkills(mapSkills.get("userSkills"));
        List<Skill> skills = mapSkills.get("skills");
        assertTrue(eventValidator.checkExistenceSkill(user, skills));
    }

    @Test
    public void checkExistenceSkillExistEnough() {
        // Пользователь имеет все нужные навыки (и не нужные)
        Map<String, List<Skill>> mapSkills = getSkill(Option.EXIST_ENOUGH);
        User user = new User();
        user.setSkills(mapSkills.get("userSkills"));
        List<Skill> skills = mapSkills.get("skills");
        assertTrue(eventValidator.checkExistenceSkill(user, skills));
    }

    @Test
    public void checkExistenceSkillExistNotEnough() {
        // У пользователя есть некоторые навыки, но их недостаточно
        Map<String, List<Skill>> mapSkills = getSkill(Option.EXIST_NOT_ENOUGH);
        User user = new User();
        user.setSkills(mapSkills.get("userSkills"));
        List<Skill> skills = mapSkills.get("skills");
        assertFalse(eventValidator.checkExistenceSkill(user, skills));
    }

    private Map<String, List<Skill>> getSkill(Option option) {
        List<Skill> userSkills = new ArrayList<>();
        List<Skill> skills = new ArrayList<>();
        Skill skillFirst = new Skill();
        skillFirst.setId(1L);
        Skill skillSecond = new Skill();
        skillSecond.setId(2L);
        Skill skillThird = new Skill();
        skillThird.setId(3L);
        Skill skillFourth = new Skill();
        skillFourth.setId(4L);

        skills.addAll(List.of(skillFirst, skillSecond, skillThird));
        switch (option) {
            case EXIST_ALL -> userSkills.addAll(List.of(skillFirst, skillSecond, skillThird));
            case EXIST_ENOUGH -> {
                userSkills.addAll(List.of(skillFirst, skillSecond, skillThird));
                skills.clear();
                skills.add(skillFirst);
            }
            case EXIST_NOT_ENOUGH -> userSkills.addAll(List.of(skillFirst, skillSecond, skillFourth));
            case NOT_EXIST -> userSkills.add(skillFourth);

        }

        return Map.of("userSkills", userSkills,
                "skills", skills);
    }

    private EventDto createValidEventDto() {
        return new EventDto(1L, "title", "description", LocalDateTime.now(), LocalDateTime.now(),
                "location", 1, 1L, List.of(1L, 2L));
    }

    enum Option {
        EXIST_ENOUGH, EXIST_ALL, EXIST_NOT_ENOUGH, NOT_EXIST
    }

}

