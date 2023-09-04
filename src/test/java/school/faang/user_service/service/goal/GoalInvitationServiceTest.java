package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapperImpl;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.GoalInvitationValidator;
import school.faang.user_service.validator.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {
    private static GoalInvitationDto invitationDto = new GoalInvitationDto(100L, 1L, 2L, 12L, RequestStatus.PENDING);
    private static User inviter = User.builder().id(1L).build();
    private static User invitedUser = User.builder().id(2L).goals(new ArrayList<>()).build();
    private static Goal goal = Goal.builder().id(1L).build();
    private static GoalInvitation invitation = GoalInvitation.builder().id(5L).invited(invitedUser).inviter(inviter)
            .goal(goal).status(RequestStatus.PENDING).build();

    @Mock
    private GoalInvitationRepository goalInvitationRepository;
    @Mock
    private UserService userService;
    @Mock
    private UserValidator userValidator;
    @Spy
    private GoalInvitationMapperImpl goalInvitationMapper;
    @Mock
    private GoalInvitationValidator goalInvitationValidator = new GoalInvitationValidator(userValidator);
    @InjectMocks
    private GoalInvitationService goalInvitationService;

    @Test
    public void givenCorrectGoalInvitation_whenCreate_thenCreateGoalInvitation() {
        when(userService.findUserById(invitationDto.getInviterId())).thenReturn(inviter);
        when(userService.findUserById(invitationDto.getInvitedUserId())).thenReturn(invitedUser);

        goalInvitationService.createInvitation(invitationDto);

        GoalInvitation expected = goalInvitationMapper.toEntity(invitationDto);
        expected.setInviter(inviter);
        expected.setInvited(invitedUser);

        Mockito.verify(goalInvitationRepository).save(expected);
    }

    @Test
    public void givenCorrectInvitation_whenAcceptingInvitation_thenSucceed() {
        when(goalInvitationRepository.findById(invitation.getId())).thenReturn(Optional.of(invitation));

        goalInvitationService.acceptGoalInvitation(invitation.getId());

        User expectedUser = User.builder().id(2L).goals(List.of(goal)).build();
        Mockito.verify(userService).saveUser(expectedUser);

        GoalInvitation expectedInvitation = GoalInvitation.builder().id(5L).invited(expectedUser).inviter(inviter)
                .goal(goal).status(RequestStatus.ACCEPTED).build();
        Mockito.verify(goalInvitationRepository).save(expectedInvitation);
    }
}
