package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

@ExtendWith(MockitoExtension.class)

public class ServiceInvitationTest {
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    GoalInvitationRepository goalInvitationRepository;
    @Mock
    GoalInvitation goalInvitation;
    @Spy
    GoalInvitationMapperImpl goalInvitationMapper;
    @InjectMocks
    private GoalInvitationService goalInvitationService;

    @Test
    void createGoalInvitation() {
        // Необходимо дописать сюда реализацию метода
    }

    @Test
    void acceptGoalInvitation() {
        // Необходимо дописать сюда реализацию метода
    }

    @Test
    void rejectGoalInvitation() {
        // Необходимо дописать сюда реализацию метода
    }

    @BeforeEach
    void getInvitations() {
        // Необходимо дописать сюда реализацию метода
    }

}