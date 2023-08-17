package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalValidator;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillRepository skillRepository;
    @Spy
    private GoalMapper goalMapper;
    @Mock
    private GoalValidator validator;
    @Mock
    private UserContext userContext;

    @InjectMocks
    private GoalService service;

     long userId = 1;
     GoalDto goalDto = new GoalDto();

    @Test
    void createGoalTest() {
        service.createGoal(goalDto);
        verify(goalRepository).save(goalMapper.toEntity(goalDto));
    }
}