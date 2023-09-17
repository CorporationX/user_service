package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoalMapperTest {
    @Spy
    private GoalMapperImpl goalMapper;
    private Goal expectedGoal;
    private GoalDto expectedGoalDto;
    private List<Long> skillIds;
    private List<Skill> skills;

    @BeforeEach
    void setUp() {
        skillIds = List.of(1L, 2L);
        skills = List.of(
                Skill.builder().id(1L).build(),
                Skill.builder().id(2L).build()
        );

        expectedGoal = Goal.builder()
                .id(1L)
                .parent(Goal.builder().id(2L).build())
                .title("title")
                .description("description")
                .status(GoalStatus.ACTIVE)
                .skillsToAchieve(skills)
                .build();

        expectedGoalDto = GoalDto.builder()
                .id(1L)
                .parentId(2L)
                .title("title")
                .description("description")
                .status(GoalStatus.ACTIVE)
                .skillIds(skillIds)
                .build();
    }

    @Test
    void testToDto() {
        GoalDto actualGoalDto = goalMapper.toDto(expectedGoal);
        assertEquals(expectedGoalDto, actualGoalDto);
    }

    @Test
    void toEntity() {
        Goal actualGoal = goalMapper.toEntity(expectedGoalDto);
        actualGoal.setSkillsToAchieve(skills);
        actualGoal.setParent(Goal.builder().id(2L).build());
        assertEquals(expectedGoal, actualGoal);
    }

    @Test
    void testToSkillIds() {
        List<Long> actualSkillIds = goalMapper.toSkillIds(skills);
        assertEquals(skillIds, actualSkillIds);
    }
}