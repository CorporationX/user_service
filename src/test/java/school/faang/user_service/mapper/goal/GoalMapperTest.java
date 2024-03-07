package school.faang.user_service.mapper.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@SpringBootTest
public class GoalMapperTest {

    @InjectMocks
    private GoalMapperImpl goalMapper;

    @Test
    void toDto_ValidEntity_SuccessfullyMapping() {
        Goal goal = getGoal();
        long skill1Id = goal.getSkillsToAchieve().get(0).getId();
        long skill2Id = goal.getSkillsToAchieve().get(1).getId();
        long skill3Id = goal.getSkillsToAchieve().get(2).getId();

        GoalDto goalDto = goalMapper.toDto(goal);

        Assertions.assertEquals(goal.getId(), goalDto.getId());
        Assertions.assertEquals(goal.getParent().getId(), goalDto.getParentId());
        Assertions.assertEquals(goal.getTitle(), goalDto.getTitle());
        Assertions.assertEquals(goal.getStatus(), goalDto.getStatus());
        Assertions.assertEquals(goal.getDescription(), goalDto.getDescription());
        Assertions.assertEquals(List.of(skill1Id, skill2Id, skill3Id), goalDto.getSkillIds());
    }

    @Test
    void toEntity_ValidDto_SuccessfullyMapping() {
        GoalDto goalDto = getGoalDto();

        Goal goal = goalMapper.toEntity(goalDto);

        Assertions.assertEquals(goalDto.getId(), goal.getId());
        Assertions.assertEquals(goalDto.getParentId(), goal.getParent().getId());
        Assertions.assertEquals(goalDto.getTitle(), goal.getTitle());
        Assertions.assertEquals(goalDto.getStatus(), goal.getStatus());
        Assertions.assertEquals(goalDto.getDescription(), goal.getDescription());
    }

    private Goal getGoal() {
        return Goal.builder()
                .id(2L)
                .parent(Goal.builder().id(1L).build())
                .title("Goal")
                .status(GoalStatus.ACTIVE)
                .description("Goal description")
                .skillsToAchieve(getSkills())
                .build();
    }

    private GoalDto getGoalDto() {
        return GoalDto.builder()
                .id(2L)
                .parentId(1L)
                .title("Goal")
                .status(GoalStatus.ACTIVE)
                .description("Goal description")
                .skillIds(getIds())
                .build();
    }

    private List<Long> getIds() {
        return List.of(1L, 2L, 3L);
    }

    private List<Skill> getSkills() {
        Skill skill1 = Skill.builder()
                .id(1L)
                .build();
        Skill skill2 = Skill.builder()
                .id(2L)
                .build();
        Skill skill3 = Skill.builder()
                .id(3L)
                .build();
        return List.of(skill1, skill2, skill3);
    }
}
