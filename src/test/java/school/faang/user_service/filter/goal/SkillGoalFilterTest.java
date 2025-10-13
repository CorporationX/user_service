package school.faang.user_service.filter.goal;


import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.user.Skill;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SkillGoalFilterTest {
    private SkillGoalFilter skillGoalFilter = new SkillGoalFilter();

    @Test
    public void isApplication_SkillIsApplicableTrue() {
        boolean result = skillGoalFilter.isApplication(new GoalFilterDto(null,
                null,
                null,
                null,
                List.of(1L, 2L)
        ));
        assertTrue(result);
    }

    @Test
    public void isApplication_SkillIsApplicableFalse() {
        boolean result = skillGoalFilter.isApplication(new GoalFilterDto(null,
                null,
                null,
                null,
                null
        ));
        assertFalse(result);
    }

    @Test
    public void apply_testFilter() {
        Skill skill1 = Skill.builder().id(1L).build();
        Skill skill2 = Skill.builder().id(2L).build();
        Stream<Goal> goalStream = Stream.of(
                Goal.builder().skillsToAchieve(List.of(skill1, skill2)).build(),
                Goal.builder().skillsToAchieve(List.of(skill2)).build()
        );
        List<Long> skillIds  = List.of(1L, 2L);
        GoalFilterDto goalFilterDto = new GoalFilterDto(null,
                null,
                null,
                null,
                skillIds
        );
        Stream<Goal> goalResult = skillGoalFilter.apply(goalStream,
                goalFilterDto);
        List<Goal> goalList = goalResult.toList();

        assertEquals(1, goalList.size());
        List<Skill> skills = goalList.get(0).getSkillsToAchieve();
        List<Long> idResults = skills.stream()
                .map(skill -> skill.getId())
                .toList();
        assertEquals(skillIds, idResults);
    }
}