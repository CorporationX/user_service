package school.faang.user_service.mapper.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GoalMapperTest {

    private static final Long GOAL_ID = 1L;
    private static final Long PARENT_GOAL_ID = 2L;
    private static final Long SKILL_ID_1 = 1L;
    private static final Long SKILL_ID_2 = 2L;
    private static final String GOAL_TITLE = "Test Goal";
    private static final String GOAL_DESCRIPTION = "Test Description";
    private static final String SKILL_TITLE = "Test Skill";

    private GoalMapper goalMapper;

    @BeforeEach
    public void setUp() {
        goalMapper = Mappers.getMapper(GoalMapper.class);
    }

    @Nested
    @DisplayName("Mapping from Goal to GoalDto Tests")
    class ToGoalDtoTests {

        @Test
        @DisplayName("Maps to GoalDto correctly when a goal is provided")
        void whenGoalProvidedThenMapToGoalDtoCorrectly() {
            Skill skill = new Skill();
            skill.setId(SKILL_ID_1);
            skill.setTitle(SKILL_TITLE);

            Goal parentGoal = new Goal();
            parentGoal.setId(PARENT_GOAL_ID);

            Goal goal = new Goal();
            goal.setId(GOAL_ID);
            goal.setTitle(GOAL_TITLE);
            goal.setDescription(GOAL_DESCRIPTION);
            goal.setParent(parentGoal);
            goal.setStatus(GoalStatus.ACTIVE);
            goal.setSkillsToAchieve(List.of(skill));

            GoalDto goalDto = goalMapper.toGoalDto(goal);

            assertEquals(GOAL_ID, goalDto.getId());
            assertEquals(GOAL_TITLE, goalDto.getTitle());
            assertEquals(GOAL_DESCRIPTION, goalDto.getDescription());
            assertEquals(PARENT_GOAL_ID, goalDto.getParentId());
            assertEquals(1, goalDto.getSkillIds().size());
            assertEquals(SKILL_ID_1, goalDto.getSkillIds().get(0));
        }
    }

    @Nested
    @DisplayName("Mapping from GoalDto to Goal Tests")
    class ToGoalTests {

        @Test
        @DisplayName("Maps to Goal correctly when a GoalDto is provided")
        void whenGoalDtoProvidedThenMapToGoalCorrectly() {
            GoalDto goalDto = GoalDto.builder()
                    .id(GOAL_ID)
                    .title(GOAL_TITLE)
                    .description(GOAL_DESCRIPTION)
                    .parentId(PARENT_GOAL_ID)
                    .skillIds(List.of(SKILL_ID_1))
                    .status(GoalStatus.ACTIVE)
                    .build();

            Goal goal = goalMapper.toGoal(goalDto);

            assertEquals(GOAL_ID, goal.getId());
            assertEquals(GOAL_TITLE, goal.getTitle());
            assertEquals(GOAL_DESCRIPTION, goal.getDescription());
            assertEquals(PARENT_GOAL_ID, goal.getParent().getId());
            assertEquals(1, goal.getSkillsToAchieve().size());
            assertEquals(SKILL_ID_1, goal.getSkillsToAchieve().get(0).getId());
        }
    }

    @Nested
    @DisplayName("Mapping Skills to Skill IDs Tests")
    class MapSkillToSkillsIdTests {

        @Test
        @DisplayName("Maps to skill IDs correctly when skills are provided")
        void whenSkillsProvidedThenMapToSkillIdsCorrectly() {
            Skill skill1 = new Skill();
            skill1.setId(SKILL_ID_1);
            Skill skill2 = new Skill();
            skill2.setId(SKILL_ID_2);

            List<Skill> skills = List.of(skill1, skill2);
            List<Long> skillIds = goalMapper.mapSkillToSkillsId(skills);

            assertEquals(2, skillIds.size());
            assertTrue(skillIds.contains(SKILL_ID_1));
            assertTrue(skillIds.contains(SKILL_ID_2));
        }
    }

    @Nested
    @DisplayName("Mapping ID to Parent Goal Tests")
    class MapIdToParentTests {

        @Test
        @DisplayName("Maps to parent goal correctly when parent ID is provided")
        void whenParentIdProvidedThenMapToParentGoalCorrectly() {
            Long parentId = PARENT_GOAL_ID;
            Goal parentGoal = goalMapper.mapIdToParent(parentId);

            assertNotNull(parentGoal);
            assertEquals(parentId, parentGoal.getId());
        }
    }

    @Nested
    @DisplayName("Mapping Skill IDs to Skills Tests")
    class MapSkillIdToListSkillTests {

        @Test
        @DisplayName("Maps to skills correctly when skill IDs are provided")
        void whenSkillIdsProvidedThenMapToSkillsCorrectly() {
            List<Long> skillIds = List.of(SKILL_ID_1, SKILL_ID_2);

            List<Skill> skills = goalMapper.mapSkillIdToListSkill(skillIds);

            assertEquals(2, skills.size());
            assertEquals(SKILL_ID_1, skills.get(0).getId());
            assertEquals(SKILL_ID_2, skills.get(1).getId());
        }
    }
}

