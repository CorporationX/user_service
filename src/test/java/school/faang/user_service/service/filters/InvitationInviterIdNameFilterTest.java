package school.faang.user_service.service.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filters.Invitation.InvitationInviterIdNameFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class InvitationInviterIdNameFilterTest {

    private InvitationInviterIdNameFilter filter;
    private User user1;
    private User user2;

    @BeforeEach
    public void setUp() {
        filter = new InvitationInviterIdNameFilter();
        user1 = User.builder()
                .id(1L)
                .username("User1")
                .build();

        user2 = User.builder()
                .id(2L)
                .username("User2")
                .build();
    }

    @Test
    @DisplayName("Test isAcceptable method with non-null inviter ID")
    public void testIsAcceptable_withNonNullInviterId() {
        InvitationFilterDto filters = new InvitationFilterDto();
        filters.setInviterId(1L);

        boolean result = filter.isAcceptable(filters);

        assertTrue(result);
    }

    @Test
    @DisplayName("Test isAcceptable method with null inviter ID")
    public void testIsAcceptable_withNullInviterId() {
        InvitationFilterDto filters = new InvitationFilterDto();
        filters.setInviterId(null);

        boolean result = filter.isAcceptable(filters);

        assertFalse(result);
    }

    @Test
    @DisplayName("Test apply method with matching inviter ID")
    public void testApply_withMatchingInviterId() {
        InvitationFilterDto filters = new InvitationFilterDto();
        filters.setInviterId(1L);

        GoalInvitation invitation1 = Mockito.mock(GoalInvitation.class);
        GoalInvitation invitation2 = Mockito.mock(GoalInvitation.class);
        GoalInvitation invitation3 = Mockito.mock(GoalInvitation.class);

        when(invitation1.getInviter()).thenReturn(user1);
        when(invitation2.getInviter()).thenReturn(user2);
        when(invitation3.getInviter()).thenReturn(user1);

        Stream<GoalInvitation> goalInvitations = Stream.of(invitation1, invitation2, invitation3);

        Stream<GoalInvitation> result = filter.apply(goalInvitations, filters);

        List<GoalInvitation> resultList = result.toList();
        assertEquals(2, resultList.size());
        assertTrue(resultList.contains(invitation1));
        assertTrue(resultList.contains(invitation3));
    }

    @Test
    @DisplayName("Test apply method with no matching inviter ID")
    public void testApply_withNoMatchingInviterId() {
        InvitationFilterDto filters = new InvitationFilterDto();
        filters.setInviterId(3L);

        GoalInvitation invitation1 = Mockito.mock(GoalInvitation.class);
        GoalInvitation invitation2 = Mockito.mock(GoalInvitation.class);

        when(invitation1.getInviter()).thenReturn(user1);
        when(invitation2.getInviter()).thenReturn(user2);

        Stream<GoalInvitation> goalInvitations = Stream.of(invitation1, invitation2);

        Stream<GoalInvitation> result = filter.apply(goalInvitations, filters);

        List<GoalInvitation> resultList = result.toList();
        assertEquals(0, resultList.size());
    }
}
