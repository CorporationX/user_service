package school.faang.user_service.mapper.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class GoalMapperTest {
    @Spy
    private GoalMapper goalMapper = Mappers.getMapper(GoalMapper.class);
    private Goal goal;
    private GoalDto goalDto;

    @BeforeEach
    void init() {
        goal = Goal.builder()
                .id(1L)
                .description("description")
                .title("title")
                .status(GoalStatus.ACTIVE)
                .parent(Goal.builder()
                        .id(3L)
                        .build())
                .skillsToAchieve(Collections.singletonList(Skill.builder()
                        .id(1)
                        .build()))
                .build();

        goalDto = GoalDto.builder()
                .id(1L)
                .description("description")
                .title("title")
                .status(GoalStatus.ACTIVE)
                .parentId(3L)
                .skillIds(List.of(1L))
                .build();
    }

    @Test
    public void testCorrectlyMapEntityToGoalDto() {
        GoalDto resultDto2 = goalMapper.toDto(goal);
        Assertions.assertEquals(goalDto, resultDto2);
    }

    @Test
    public void testToCorrectlyMapGoalDtoToEntity() {
        Goal resultGoal = goalMapper.toEntity(goalDto);
        Assertions.assertEquals(goal, resultGoal);
    }
}