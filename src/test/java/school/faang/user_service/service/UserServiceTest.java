package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private MentorshipService mentorshipService;

    @InjectMocks
    private UserService userService;

    @Test
    public void testDeactivateUserWithDea() {

    }

}