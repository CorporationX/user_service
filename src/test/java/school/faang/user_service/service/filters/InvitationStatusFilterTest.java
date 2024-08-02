package school.faang.user_service.service.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filters.Invitation.InvitationStatusFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.RequestStatus.*;

public class InvitationStatusFilterTest {

    private InvitationStatusFilter filter;

    @BeforeEach
    public void setUp() {
        filter = new InvitationStatusFilter();
    }

    @Test
    @DisplayName("Test isAcceptable method with non-null status")
    public void testIsAcceptable_withNonNullStatus() {
        InvitationFilterDto filters = new InvitationFilterDto();
        filters.setStatus(PENDING);

        boolean result = filter.isAcceptable(filters);

        assertTrue(result);
    }

    @Test
    @DisplayName("Test isAcceptable method with null status")
    public void testIsAcceptable_withNullStatus() {
        InvitationFilterDto filters = new InvitationFilterDto();
        filters.setStatus(null);

        boolean result = filter.isAcceptable(filters);

        assertFalse(result);
    }

    @Test
    @DisplayName("Test apply method with matching status")
    public void testApply_withMatchingStatus() {
        InvitationFilterDto filters = new InvitationFilterDto();
        filters.setStatus(ACCEPTED);

        GoalInvitation invitation1 = Mockito.mock(GoalInvitation.class);
        GoalInvitation invitation2 = Mockito.mock(GoalInvitation.class);
        GoalInvitation invitation3 = Mockito.mock(GoalInvitation.class);

        when(invitation1.getStatus()).thenReturn(PENDING);
        when(invitation2.getStatus()).thenReturn(ACCEPTED);
        when(invitation3.getStatus()).thenReturn(ACCEPTED);

        Stream<GoalInvitation> goalInvitations = Stream.of(invitation1, invitation2, invitation3);

        Stream<GoalInvitation> result = filter.apply(goalInvitations, filters);

        List<GoalInvitation> resultList = result.toList();
        assertEquals(2, resultList.size());
        assertTrue(resultList.contains(invitation2));
        assertTrue(resultList.contains(invitation3));
    }

    @Test
    @DisplayName("Test apply method with no matching status")
    public void testApply_withNoMatchingStatus() {
        InvitationFilterDto filters = new InvitationFilterDto();
        filters.setStatus(REJECTED);

        GoalInvitation invitation1 = Mockito.mock(GoalInvitation.class);
        GoalInvitation invitation2 = Mockito.mock(GoalInvitation.class);

        when(invitation1.getStatus()).thenReturn(PENDING);
        when(invitation2.getStatus()).thenReturn(ACCEPTED);

        Stream<GoalInvitation> goalInvitations = Stream.of(invitation1, invitation2);

        Stream<GoalInvitation> result = filter.apply(goalInvitations, filters);

        List<GoalInvitation> resultList = result.toList();
        assertEquals(0, resultList.size());
    }
}
