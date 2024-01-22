package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.filter.GoalFilter;
import school.faang.user_service.filter.impl.goal.GoalStatusFilter;
import school.faang.user_service.filter.impl.goal.GoalTitleFilter;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Ilia Chuvatkin
 */

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    @Mock
    private GoalValidator goalValidator;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private GoalMapper goalMapper;
    @InjectMocks
    private GoalService goalService;

    @Test
    void testCreateGoal() {
        Goal goal = new Goal();
        goal.setId(1L);
        goal.setTitle("Goal");
        goal.setDescription("Something");
        Skill skill_1 = new Skill();
        skill_1.setId(1L);
        Skill skill_2 = new Skill();
        skill_2.setId(2L);
        goal.setSkillsToAchieve(List.of(skill_1, skill_2));
        GoalDto goalDto = new GoalDto();
        goalDto.setSkillIds(List.of(1L, 2L));
        Long userId = 1L;

        when(goalValidator.isValidateByActiveGoals(userId)).thenReturn(true);
        when(goalValidator.isValidateByExistingSkills(userId, goal)).thenReturn(true);
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);

        goalService.createGoal(userId, goalDto);

        verify(goalRepository, times(1)).create(goal.getTitle(), goal.getDescription(), userId);
        verify(goalRepository, times(4)).addSkillToGoal(anyLong(), anyLong());
    }

    @Test
    void testUpdateGoal() {
        Goal goalOld = new Goal();
        Long goalOldId = 1L;
        Goal goal = new Goal();
        goal.setId(1L);
        goal.setStatus(GoalStatus.COMPLETED);
        GoalDto goalDto = new GoalDto();
        goalDto.setId(1L);
        goalDto.setSkillIds(List.of(1L, 2L));
        Skill skill_1 = new Skill();
        skill_1.setId(1L);
        Skill skill_2 = new Skill();
        skill_2.setId(2L);
        goal.setSkillsToAchieve(List.of(skill_1, skill_2));
        User user1 = new User();
        user1.setSkills(List.of(skill_1));
        User user2 = new User();
        user2.setSkills(List.of(skill_1));

        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        when(goalRepository.findById(goalOldId)).thenReturn(Optional.of(goalOld));
        when(goalValidator.isValidateByCompleted(goalOld)).thenReturn(true);
        when(goalValidator.isValidateByExistingSkills(goal)).thenReturn(true);
        when(goalRepository.findUsersByGoalId(goal.getId())).thenReturn(List.of(user1, user2));


        goalService.updateGoal(goalOldId, goalDto);

        verify(goalRepository, times(1)).save(goal);
        verify(goalRepository, times(2)).addSkillToGoal(anyLong(), anyLong());
        verify(skillRepository, times(2)).assignSkillToUser(anyLong(), anyLong());
    }

    @Test
    void testDeleteGoal() {
        goalService.deleteGoal(1L);
        verify(goalRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetGoalsByUser() {
        Long userId = 1L;
        GoalFilterDto filter = new GoalFilterDto("Some title", GoalStatus.ACTIVE);

        GoalDto goalDtoExpected = new GoalDto();
        goalDtoExpected.setId(2L);
        goalDtoExpected.setTitle("Some title");
        goalDtoExpected.setStatus(GoalStatus.ACTIVE);
        List<GoalDto> expectedList = List.of(goalDtoExpected);

        Goal goal1 = new Goal();
        goal1.setTitle("Title1");
        goal1.setId(1L);
        goal1.setStatus(GoalStatus.ACTIVE);
        Goal goal2 = new Goal();
        goal2.setId(2L);
        goal2.setTitle("Some title");
        goal2.setStatus(GoalStatus.ACTIVE);
        Skill skill_1 = new Skill();
        skill_1.setId(1L);

        List<GoalFilter> goalFilters = List.of(new GoalStatusFilter(), new GoalTitleFilter());
        goalService = new GoalService(goalValidator, goalRepository, skillRepository, goalMapper, goalFilters);

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of(goal1, goal2));
        when(skillRepository.findSkillsByGoalId(anyLong())).thenReturn(List.of(skill_1));
        when(goalMapper.toDto(goal2)).thenReturn(goalDtoExpected);

        assertEquals(expectedList, goalService.findGoalsByUser(userId, filter));
    }
}
