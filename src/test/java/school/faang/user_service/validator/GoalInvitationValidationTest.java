package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.goal.GoalRepository;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class GoalInvitationValidationTest {

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    GoalInvitationValidation goalInvitationValidation;

    @BeforeEach
    void setUp() {
    }
}