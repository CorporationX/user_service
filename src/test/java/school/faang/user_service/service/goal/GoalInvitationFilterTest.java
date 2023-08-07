package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import school.faang.user_service.entity.User;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.dto.filter.GoalInvitationFilterIDto;
import school.faang.user_service.service.goal.filters.GoalInvitationStatusFilter;
import school.faang.user_service.service.goal.filters.GoalInvitationInviterIdFilter;
import school.faang.user_service.service.goal.filters.GoalInvitationInviterNameFilter;
import school.faang.user_service.service.goal.filters.GoalInvitationInvitedUserIdFilter;
import school.faang.user_service.service.goal.filters.GoalInvitationInvitedUserNameFilter;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationFilterTest {

    User vlad;
    User oleg;
    User pasha;
    User vitalii;

    GoalInvitation faangInvitation;
    GoalInvitation youTubeInvitation;

    GoalInvitationFilterIDto filters;
    List<GoalInvitation> invitations;

    @BeforeEach
    void setUp() {
        vlad = User.builder().id(1L).username("Vlad").build();
        oleg = User.builder().id(2L).username("Oleg").build();
        pasha = User.builder().id(3L).username("Pasha").build();
        vitalii = User.builder().id(4L).username("Vitalii").build();

        filters = new GoalInvitationFilterIDto();

        youTubeInvitation = GoalInvitation.builder().inviter(vlad).invited(oleg).status(RequestStatus.PENDING).build();
        faangInvitation = GoalInvitation.builder().inviter(pasha).invited(vitalii).status(RequestStatus.ACCEPTED).build();
    }

    @Test
    @DisplayName("Invited user ID filter")
    void testInvitedUserIdFilter() {
        filters.setInvitedId(2L);
        invitations = List.of(youTubeInvitation, faangInvitation, youTubeInvitation);

        GoalInvitationInvitedUserIdFilter filter = new GoalInvitationInvitedUserIdFilter();
        List<GoalInvitation> filteredInvitations = filter.apply(invitations.stream(), filters).toList();

        Assertions.assertEquals(2, filteredInvitations.size());
        Assertions.assertEquals(youTubeInvitation, filteredInvitations.get(0));
        Assertions.assertEquals(vlad, filteredInvitations.get(1).getInviter());
    }

    @Test
    @DisplayName("Inviter user ID filter")
    void testInviterUserIdFilter() {
        filters.setInviterId(1L);
        invitations = List.of(faangInvitation, faangInvitation, youTubeInvitation);

        GoalInvitationInviterIdFilter filter = new GoalInvitationInviterIdFilter();
        List<GoalInvitation> filteredInvitations = filter.apply(invitations.stream(), filters).toList();

        Assertions.assertEquals(1, filteredInvitations.size());
        Assertions.assertEquals(youTubeInvitation, filteredInvitations.get(0));
        Assertions.assertEquals(oleg, filteredInvitations.get(0).getInvited());
    }

    @Test
    @DisplayName("Inviter username filter")
    void testInviterUsernameFilter() {
        filters.setInviterNamePattern("Pasha");
        invitations = List.of(youTubeInvitation, faangInvitation, youTubeInvitation);

        GoalInvitationInviterNameFilter filter = new GoalInvitationInviterNameFilter();
        List<GoalInvitation> filteredInvitations = filter.apply(invitations.stream(), filters).toList();

        Assertions.assertEquals(1, filteredInvitations.size());
        Assertions.assertEquals(faangInvitation, filteredInvitations.get(0));
        Assertions.assertEquals(vitalii, filteredInvitations.get(0).getInvited());
    }

    @Test
    @DisplayName("Invited username filter")
    void testInvitedUsernameFilter() {
        filters.setInvitedNamePattern("Vitalii");
        invitations = List.of(youTubeInvitation, faangInvitation, youTubeInvitation);

        GoalInvitationInvitedUserNameFilter filter = new GoalInvitationInvitedUserNameFilter();
        List<GoalInvitation> filteredInvitations = filter.apply(invitations.stream(), filters).toList();

        Assertions.assertEquals(1, filteredInvitations.size());
        Assertions.assertEquals(faangInvitation, filteredInvitations.get(0));
        Assertions.assertEquals(pasha, filteredInvitations.get(0).getInviter());
    }

    @Test
    @DisplayName("Goal status filter")
    void testStatusFilter() {
        filters.setStatus(RequestStatus.ACCEPTED);
        invitations = List.of(youTubeInvitation, faangInvitation, youTubeInvitation, faangInvitation);

        GoalInvitationStatusFilter filter = new GoalInvitationStatusFilter();
        List<GoalInvitation> filteredInvitations = filter.apply(invitations.stream(), filters).toList();

        Assertions.assertEquals(2, filteredInvitations.size());
        Assertions.assertEquals(faangInvitation, filteredInvitations.get(1));
        Assertions.assertFalse(filteredInvitations.contains(youTubeInvitation));
    }
}
