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

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class GoalMapperTest {

    @Spy
    private GoalMapperImpl goalMapper;
    private Goal goal;
    private GoalDto goalDto;
    private List<Skill> skills;
    private List<Long> skillIds;

    @BeforeEach
    public void setUp() {
        Goal parent = Goal.builder()
                .id(1L)
                .build();

        Skill firstSkill = Skill.builder()
                .id(1L)
                .title("SkillTitle1")
                .build();
        Skill secondSkill = Skill.builder()
                .id(2L)
                .title("SkillTitle2")
                .build();

        skills = List.of(firstSkill, secondSkill);
        skillIds = List.of(1L, 2L);

        goal = Goal.builder()
                .id(2L)
                .parent(parent)
                .title("title")
                .description("description")
                .status(GoalStatus.ACTIVE)
                .skillsToAchieve(skills)
                .build();

        goalDto = GoalDto.builder()
                .id(2L)
                .parentId(1L)
                .title("title")
                .description("description")
                .status(GoalStatus.ACTIVE)
                .skillIds(skillIds)
                .build();
    }

    @Test
    public void testToDto() {
        GoalDto result = goalMapper.toDto(goal);
        assertEquals(goalDto, result);
    }

    @Test
    public void testToEntity() {
        Goal expected = goal;
        expected.setParent(null);
        expected.setSkillsToAchieve(null);
        Goal result = goalMapper.toEntity(goalDto);

        assertEquals(expected, result);
    }

    @Test
    public void testSkillToId() {
        List<Long> result = goalMapper.toSkillIds(skills);

        assertEquals(skillIds, result);
    }
}
