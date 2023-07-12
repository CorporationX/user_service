package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    private GoalService goalService;
}