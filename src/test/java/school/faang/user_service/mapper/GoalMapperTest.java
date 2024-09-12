package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GoalMapperTest {

    private GoalMapper goalMapper;

    @BeforeEach
    public void setUp() {
        goalMapper = Mappers.getMapper(GoalMapper.class);
    }

    @Test
    @DisplayName("Test mapping from Goal to GoalDto")
    public void testToGoalDto() {
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("Test Skill");

        Goal parentGoal = new Goal();
        parentGoal.setId(2L);

        Goal goal = new Goal();
        goal.setId(1L);
        goal.setTitle("Test Goal");
        goal.setDescription("Test Description");
        goal.setParent(parentGoal);
        goal.setSkillsToAchieve(List.of(skill));

        GoalDto goalDto = goalMapper.toGoalDto(goal);

        assertEquals(goal.getId(), goalDto.getId());
        assertEquals(goal.getTitle(), goalDto.getTitle());
        assertEquals(goal.getDescription(), goalDto.getDescription());
        assertEquals(goal.getParent().getId(), goalDto.getParentId());
        assertEquals(1, goalDto.getSkillIds().size());
        assertEquals(skill.getId(), goalDto.getSkillIds().get(0));
    }

    @Test
    @DisplayName("Test mapping from GoalDto to Goal")
    public void testToGoal() {
        GoalDto goalDto = GoalDto.builder()
                .id(1L)
                .title("Test Goal")
                .description("Test Description")
                .parentId(2L)
                .skillIds(List.of(1L))
                .build();

        Goal goal = goalMapper.toGoal(goalDto);

        assertEquals(goalDto.getId(), goal.getId());
        assertEquals(goalDto.getTitle(), goal.getTitle());
        assertEquals(goalDto.getDescription(), goal.getDescription());
        assertEquals(goalDto.getParentId(), goal.getParent().getId());
        assertEquals(1, goal.getSkillsToAchieve().size());
        assertEquals(goalDto.getSkillIds().get(0), goal.getSkillsToAchieve().get(0).getId());
    }

    @Test
    @DisplayName("Test mapSkillToSkillsId")
    public void testMapSkillToSkillsId() {
        Skill skill1 = new Skill();
        skill1.setId(1L);
        Skill skill2 = new Skill();
        skill2.setId(2L);

        List<Skill> skills = List.of(skill1, skill2);
        List<Long> skillIds = goalMapper.mapSkillToSkillsId(skills);

        assertEquals(2, skillIds.size());
        assertTrue(skillIds.contains(1L));
        assertTrue(skillIds.contains(2L));
    }

    @Test
    @DisplayName("Test mapIdToParent")
    public void testMapIdToParent() {
        Long parentId = 1L;
        Goal parentGoal = goalMapper.mapIdToParent(parentId);

        assertNotNull(parentGoal);
        assertEquals(parentId, parentGoal.getId());
    }

    @Test
    @DisplayName("Test mapSkillIdToListSkill")
    public void testMapSkillIdToListSkill() {
        List<Long> skillIds = List.of(1L, 2L);

        List<Skill> skills = goalMapper.mapSkillIdToListSkill(skillIds);

        assertEquals(2, skills.size());
        assertEquals(1L, skills.get(0).getId());
        assertEquals(2L, skills.get(1).getId());
    }
}
