package school.faang.user_service.service.goal.filter.invitation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static school.faang.user_service.util.goal.invitation.InvitationFabric.getInvitations;

class InvitedIdFilterTest {
    private static final long INVITED_USER_ID = 1;
    private static final int NUMBER_OF_INVITATIONS = 5;
    private static final int ONE_INVITATION = 1;

    private final InvitationFilterDto applicableFilterDto =
            new InvitationFilterDto(null, null, null, INVITED_USER_ID, null);
    private final InvitationFilterDto nonApplicableFilterDto =
            new InvitationFilterDto(null, null, null, null, null);

    private final InvitedIdFilter invitedIdFilter = new InvitedIdFilter();

    @Test
    @DisplayName("Given non applicable filter when check then return false")
    void testIsApplicableNonApplicable() {
        assertThat(invitedIdFilter.isApplicable(nonApplicableFilterDto)).isEqualTo(false);
    }

    @Test
    @DisplayName("Given applicable filter when check then return true")
    void testIsApplicable() {
        assertThat(invitedIdFilter.isApplicable(applicableFilterDto)).isEqualTo(true);
    }

    @Test
    @DisplayName("Given invitations when apply then return invitation stream")
    void testApplySuccessful() {
        Stream<GoalInvitation> invitationStream = getInvitations(NUMBER_OF_INVITATIONS);
        Stream<GoalInvitation> expectedInvitationStream = getInvitations(ONE_INVITATION);
        assertThat(invitedIdFilter.apply(invitationStream, applicableFilterDto).toList())
                .isEqualTo(expectedInvitationStream.toList());
    }
}