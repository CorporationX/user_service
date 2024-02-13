package school.faang.user_service.service.skill;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.SkillService;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillService skillService;

    long goalId = 1L;

    @Test
    void incorrectIdTest() {
        when(skillRepository.findSkillsByGoalId(goalId))
                .thenReturn(new ArrayList<>());
        skillService.findSkillsByGoalId(goalId);
        verify(skillRepository, times(1)).findSkillsByGoalId(goalId);
    }
}