package school.faang.user_service.service.goal.filter_goalinvitation;

import org.junit.jupiter.api.BeforeEach;
import school.faang.user_service.dto.goal.filter.GoalInvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;

public class SetUpFiltresTest {

    GoalInvitationFilterDto filterInviterNamePatternDto;
    GoalInvitationFilterDto filterInvitedNamePatternDto;
    GoalInvitationFilterDto filterInviterIdDto;
    GoalInvitationFilterDto filterInvitedIdDto;
    GoalInvitationFilterDto filterUserIdDto;
    GoalInvitationFilterDto filterStatusDto;

    GoalInvitation goalInvitation;
    User inviter;
    User invited;


    @BeforeEach
    void setUp() {
        filterInviterNamePatternDto = GoalInvitationFilterDto.builder()
                .inviterNamePattern("inviterNamePattern")
                .build();

        filterInvitedNamePatternDto = GoalInvitationFilterDto.builder()
                .invitedNamePattern("invitedNamePattern")
                .status(RequestStatus.ACCEPTED)
                .build();

        filterStatusDto = GoalInvitationFilterDto.builder()
                .status(RequestStatus.PENDING)
                .build();

        filterInviterIdDto = GoalInvitationFilterDto.builder()
                .inviterId(2L)
                .build();

        filterInvitedIdDto = GoalInvitationFilterDto.builder()
                .invitedId(3L)
                .build();

        filterUserIdDto = GoalInvitationFilterDto.builder()
                .invitedId(5L)
                .inviterId(6L)
                .build();

        inviter = new User();
        inviter.setId(2L);
        inviter.setUsername("inviterNamePattern");

        invited = new User();
        invited.setId(3L);
        invited.setUsername("invitedNamePattern");

        goalInvitation = GoalInvitation.builder()
                .id(1L)
                .inviter(inviter)
                .invited(invited)
                .status(RequestStatus.PENDING)
                .build();
    }
}