package school.faang.user_service.service.goal.filter.invitation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static school.faang.user_service.util.goal.invitation.InvitationFabric.getInvitation;

class InvitedNamePatternFilterTest {
    private static final String INVITED_NAME_PATTERN = "bob";
    private static final String USERNAME_1 = "Ross";
    private static final String USERNAME_2 = "Bobby";
    private static final String USERNAME_3 = "Anna";

    private final InvitationFilterDto applicableFilterDto =
            new InvitationFilterDto(null, INVITED_NAME_PATTERN, null, null, null);
    private final InvitationFilterDto nonApplicableFilterDto =
            new InvitationFilterDto(null, null, null, null, null);

    private final InvitedNamePatternFilter invitedNamePatternFilter = new InvitedNamePatternFilter();

    @Test
    @DisplayName("Given non applicable filter when check then return false")
    void testIsApplicableNonApplicable() {
        assertThat(invitedNamePatternFilter.isApplicable(nonApplicableFilterDto)).isEqualTo(false);
    }

    @Test
    @DisplayName("Given applicable filter when check then return true")
    void testIsApplicable() {
        assertThat(invitedNamePatternFilter.isApplicable(applicableFilterDto)).isEqualTo(true);
    }

    @Test
    @DisplayName("Given invitations when apply then return invitation stream")
    void testApplySuccessful() {
        var invitation1 = getInvitation(null, USERNAME_1);
        var invitation2 = getInvitation(null, USERNAME_2);
        var invitation3 = getInvitation(null, USERNAME_3);

        Stream<GoalInvitation> invitationStream = Stream.of(invitation1, invitation2, invitation3);
        Stream<GoalInvitation> expectedInvitationStream = Stream.of(invitation2);
        assertThat(invitedNamePatternFilter.apply(invitationStream, applicableFilterDto).toList())
                .isEqualTo(expectedInvitationStream.toList());
    }
}