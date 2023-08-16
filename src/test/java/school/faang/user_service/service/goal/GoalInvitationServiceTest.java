package school.faang.user_service.service.goal;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoalInvitationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalInvitationRepository goalInvitationRepository;
    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalInvitationService goalInvitationService;


}