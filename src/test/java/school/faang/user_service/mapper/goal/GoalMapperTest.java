package school.faang.user_service.mapper.goal;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class GoalMapperTest {

    @InjectMocks
    private GoalMapperImpl goalMapper;

    @Test
    void toDto_ValidEntity_SuccessfullyMapping() {
        Goal goal = getGoal();

        GoalDto goalDto = goalMapper.toDto(goal);

        assertEquals(goal.getId(), goalDto.getId());
        assertEquals(goal.getParent().getId(), goalDto.getParentId());
        assertEquals(goal.getTitle(), goalDto.getTitle());
        assertEquals(goal.getStatus(), goalDto.getStatus());
        assertEquals(goal.getDescription(), goalDto.getDescription());
        assertEquals(getIds(), goalDto.getSkillIds());
    }

    @Test
    void toEntity_ValidDto_SuccessfullyMapping() {
        GoalDto goalDto = getGoalDto();

        Goal goal = goalMapper.toEntity(goalDto);

        assertEquals(goalDto.getId(), goal.getId());
        assertEquals(goalDto.getTitle(), goal.getTitle());
        assertEquals(goalDto.getStatus(), goal.getStatus());
        assertEquals(goalDto.getDescription(), goal.getDescription());
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
