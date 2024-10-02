package school.faang.user_service.service.user.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserExtendedFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.UserFilter;

import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserSkillFilterTest {
    private UserFilter userSkillFilter;
    private String skillTest;
    private List<Skill> skills;

    @BeforeEach
    void setUp() {
        userSkillFilter = new UserSkillFilter();
        skillTest = "Snowboard";

        Skill skill = new Skill();
        skill.setTitle("Snowboard");

        Skill skill1 = new Skill();
        skill1.setTitle("Tennis");

        skills = List.of(skill, skill1);
    }

    @Test
    void testIsApplicable_patternWithFilledSkill() {
        UserExtendedFilterDto userFilterDto = new UserExtendedFilterDto();
        userFilterDto.setSkillPattern(skillTest);
        boolean isApplicable = userSkillFilter.isApplicable(userFilterDto);

        assertTrue(isApplicable);
    }

    @Test
    void testIsApplicable_patternWithNotFilledSkill() {
        UserExtendedFilterDto userFilterDto = new UserExtendedFilterDto();
        boolean isApplicable = userSkillFilter.isApplicable(userFilterDto);

        assertFalse(isApplicable);
    }

    @Test
    void testGetPredicate_successSkillValidation() {
        UserExtendedFilterDto userFilterDto = new UserExtendedFilterDto();
        userFilterDto.setSkillPattern(skillTest);

        User user = new User();
        user.setSkills(skills);

        Predicate<User> predicate =  userSkillFilter.getPredicate(userFilterDto);

        boolean result = predicate.test(user);

        assertTrue(result);
    }

    @Test
    void testGetPredicate_failedSkillValidation() {
        Skill skill2 = new Skill();
        skill2.setTitle("Driving");
        List<Skill> skills1 = List.of(skill2);

        UserExtendedFilterDto userFilterDto = new UserExtendedFilterDto();
        userFilterDto.setSkillPattern(skillTest);

        User user = new User();
        user.setSkills(skills1);

        Predicate<User> predicate =  userSkillFilter.getPredicate(userFilterDto);

        boolean result = predicate.test(user);

        assertFalse(result);
    }
}
