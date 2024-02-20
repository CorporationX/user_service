package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.goal.GoalMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GoalMapperTest {
    GoalMapper mapper;

    GoalMapperTest() {
        mapper = Mappers.getMapper(GoalMapper.class);
    }

    @Test
    void toEntityTest() {
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("title");
        goalDto.setSkillIds(List.of(1L));
        goalDto.setParentId(1L);

        Goal goalExpected = new Goal();
        goalExpected.setTitle("title");
        goalExpected.setParent(new Goal());

        assertEquals(goalExpected, mapper.toEntity(goalDto));
    }

    @Test
    void toDtoTest() {
        Goal goal = new Goal();
        goal.setTitle("title");
        goal.setSkillsToAchieve(List.of(Skill.builder().id(1L).build(), Skill.builder().id(2L).build()));
        Goal parentGoal = new Goal();
        parentGoal.setId(3L);
        goal.setParent(parentGoal);

        GoalDto goalDtoExpected = new GoalDto();
        goalDtoExpected.setTitle("title");
        goalDtoExpected.setParentId(3L);
        goalDtoExpected.setSkillIds(List.of(1L, 2L));

        assertEquals(goalDtoExpected, mapper.toDto(goal));
    }
}
