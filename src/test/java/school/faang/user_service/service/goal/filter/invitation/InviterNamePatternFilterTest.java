package school.faang.user_service.service.goal.filter.invitation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static school.faang.user_service.util.goal.invitation.InvitationFabric.getInvitation;
import static school.faang.user_service.util.goal.invitation.InvitationFabric.getInvitationFilterDto;

class InviterNamePatternFilterTest {
    private static final String INVITER_NAME_PATTERN = "bob";
    private static final String USERNAME_1 = "Ross";
    private static final String USERNAME_2 = "Bobby";
    private static final String USERNAME_3 = "Anna";

    private final InvitationFilterDto applicableFilterDto = getInvitationFilterDto(INVITER_NAME_PATTERN, null);
    private final InvitationFilterDto nonApplicableFilterDto = getInvitationFilterDto();
    private final InviterNamePatternFilter inviterNamePatternFilter = new InviterNamePatternFilter();

    @Test
    @DisplayName("Given non applicable filter when check then return false")
    void testIsApplicableNonApplicable() {
        assertThat(inviterNamePatternFilter.isApplicable(nonApplicableFilterDto)).isEqualTo(false);
    }

    @Test
    @DisplayName("Given applicable filter when check then return true")
    void testIsApplicable() {
        assertThat(inviterNamePatternFilter.isApplicable(applicableFilterDto)).isEqualTo(true);
    }

    @Test
    @DisplayName("Given invitations when apply then return invitation stream")
    void testApplySuccessful() {
        GoalInvitation invitation1 = getInvitation(USERNAME_1, null);
        GoalInvitation invitation2 = getInvitation(USERNAME_2, null);
        GoalInvitation invitation3 = getInvitation(USERNAME_3, null);

        Stream<GoalInvitation> invitationStream = Stream.of(invitation1, invitation2, invitation3);
        Stream<GoalInvitation> expectedInvitationStream = Stream.of(invitation2);
        assertThat(inviterNamePatternFilter.apply(invitationStream, applicableFilterDto).toList())
                .isEqualTo(expectedInvitationStream.toList());
    }
}