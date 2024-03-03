package school.faang.user_service.mapper.goal;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GoalMapperTest {
    private final GoalMapper goalMapper = new GoalMapperImpl();

    @Test
    void toEntity() {
        GoalDto goalDto = new GoalDto(1L, "test", 1L, "test", GoalStatus.ACTIVE, List.of(1L, 2L));
        List<Skill> skills = List.of(Skill.builder().id(1L).build(), Skill.builder().id(2L).build());
        Goal goal = goalMapper.toEntity(goalDto);

        assertAll(
                () -> assertEquals(goalDto.getId(), goal.getId()),
                () -> assertEquals(goalDto.getDescription(), goal.getDescription()),
                () -> assertEquals(goalDto.getTitle(), goal.getTitle()),
                () -> assertEquals(goalDto.getStatus(), goal.getStatus()),
                () -> assertEquals(skills, goal.getSkillsToAchieve())
        );
    }

    @Test
    void toDto() {
        Goal goal = new Goal();
        goal.setId(1L);
        goal.setDescription("test");
        goal.setTitle("test");
        goal.setStatus(GoalStatus.ACTIVE);
        Skill skill = new Skill();
        skill.setId(1L);
        goal.setSkillsToAchieve(Collections.singletonList(skill));

        GoalDto goalDto = goalMapper.toDto(goal);
        assertAll(
                () -> assertEquals(goalDto.getId(), goal.getId()),
                () -> assertEquals(goalDto.getDescription(), goal.getDescription()),
                () -> assertEquals(goalDto.getTitle(), goal.getTitle()),
                () -> assertEquals(goalDto.getStatus(), goal.getStatus()),
                () -> assertEquals(Collections.singletonList(1L), goalDto.getSkillIds())
        );
    }

    @Test
    void toSkillIds() {
        Skill skill = new Skill();
        skill.setId(1L);
        List<Skill> skills = Collections.singletonList(skill);
        assertEquals(List.of(1L), goalMapper.toSkillIds(skills));
    }
}