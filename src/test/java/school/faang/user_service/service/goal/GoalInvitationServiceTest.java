package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalInvitationValidation;
import static school.faang.user_service.entity.RequestStatus.PENDING;

@ExtendWith(MockitoExtension.class)
class GoalInvitationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalInvitationRepository goalInvitationRepository;
    @Mock
    private GoalRepository goalRepository;

    @Mock
    GoalInvitationValidation goalInvitationValidation;

    @Mock
    GoalInvitationMapper goalInvitationMapper;

    @InjectMocks
    private GoalInvitationService goalInvitationService;

    private Goal goal;
    private User inviter;
    private User invitedUser;
    private GoalInvitation goalInvitation;


    @BeforeEach
    void setUp() {
       goal =  Goal.builder()
                .id(1L)
                .build();

        inviter =  User.builder()
                .id(1L)
                .build();

        invitedUser = User.builder()
                .id(2L)
                .build();

        goalInvitation = GoalInvitation.builder()
                .id(1L)
                .inviter(inviter)
                .invited(invitedUser)
                .goal(goal)
                .status(PENDING)
                .createdAt(null)
                .updatedAt(null)
                .build();
    }

    @Test
    void testСreateInvitationToEntityCorrect() {
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto(1L, 1L, 2L, 2L, PENDING);
        Mockito.when(goalInvitationMapper.toEntity(goalInvitationDto)).thenReturn(goalInvitation);
        goalInvitationService.createInvitation(goalInvitationDto);
        Mockito.verify(goalInvitationRepository, Mockito.times(1)).save(goalInvitation);
    }

}