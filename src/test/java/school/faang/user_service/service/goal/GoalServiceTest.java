package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @InjectMocks
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillRepository skillRepository;

    @Spy
    private GoalMapper goalMapper = Mappers.getMapper(GoalMapper.class);

    private GoalDto goalDto;
    private List<String> skills;
    private Long id;
    private String title;

    @BeforeEach
    void setUp(){
        id = 1L;
        title = "title";
        skills = Arrays.asList("skill1", "skill2", "skill3");
        goalDto = new GoalDto(id, title);
    }

    @Test
    public void testCreateGoal_Successful() {
        when(goalRepository.countActiveGoalsPerUser(id)).thenReturn(2);
        when(skillRepository.findByTitle(anyString())).thenReturn(Optional.of(new Skill()));
        goalDto.setSkills(skills);

        GoalDto result = goalService.createGoal(goalDto, id);


        assertEquals(result.getId(), id);
        assertEquals(result.getTitle(), title);
    }

    @Test
    public void testCreateGoal_UnexistingSkills() {
        when(skillRepository.findByTitle(anyString())).thenReturn(Optional.empty());
        goalDto.setSkills(skills);

        assertThrows(IllegalArgumentException.class,
                () -> goalService.createGoal(goalDto, id));
    }

    @Test
    public void testCreateGoal_TooManyGoals() {
        when(skillRepository.findByTitle(anyString())).thenReturn(Optional.of(new Skill()));
        when(goalRepository.countActiveGoalsPerUser(id)).thenReturn(5);
        goalDto.setSkills(skills);

        assertThrows(IllegalArgumentException.class,
                () -> goalService.createGoal(goalDto, id));
    }

    @Test
    public void testUpdateGoal_Successful() {
        String newTitle = "New Title";
        long existingGoalId = 1L;
        Goal existingGoal = new Goal();
        existingGoal.setId(existingGoalId);

        GoalDto newGoalDto = new GoalDto(id, newTitle);

        when(goalRepository.findById(existingGoalId)).thenReturn(Optional.of(existingGoal));
        when(goalRepository.save(Mockito.any(Goal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GoalDto result = goalService.updateGoal(newGoalDto, id);

        assertEquals(newGoalDto, result);
    }

    @Test
    public void testUpdateGoal_GoalNotFound() {

        Mockito.lenient().when(goalRepository.findById(goalDto.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> goalService.updateGoal(goalDto, id));

        assertEquals("Goal 1 not found", exception.getMessage());
    }
}