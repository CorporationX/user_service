package school.faang.user_service.service.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filters.InvitationStatusFilter;

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
    public void testIsAcceptable_withNonNullStatus() {
        // Arrange
        InvitationFilterDto filters = new InvitationFilterDto();
        filters.setStatus(PENDING);

        // Act
        boolean result = filter.isAcceptable(filters);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testIsAcceptable_withNullStatus() {
        // Arrange
        InvitationFilterDto filters = new InvitationFilterDto();
        filters.setStatus(null);

        // Act
        boolean result = filter.isAcceptable(filters);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testApply_withMatchingStatus() {
        // Arrange
        InvitationFilterDto filters = new InvitationFilterDto();
        filters.setStatus(ACCEPTED);

        GoalInvitation invitation1 = Mockito.mock(GoalInvitation.class);
        GoalInvitation invitation2 = Mockito.mock(GoalInvitation.class);
        GoalInvitation invitation3 = Mockito.mock(GoalInvitation.class);

        when(invitation1.getStatus()).thenReturn(PENDING);
        when(invitation2.getStatus()).thenReturn(ACCEPTED);
        when(invitation3.getStatus()).thenReturn(ACCEPTED);

        Stream<GoalInvitation> goalInvitations = Stream.of(invitation1, invitation2, invitation3);

        // Act
        Stream<GoalInvitation> result = filter.apply(goalInvitations, filters);

        // Assert
        List<GoalInvitation> resultList = result.toList();
        assertEquals(2, resultList.size());
        assertTrue(resultList.contains(invitation2));
        assertTrue(resultList.contains(invitation3));
    }

    @Test
    public void testApply_withNoMatchingStatus() {
        // Arrange
        InvitationFilterDto filters = new InvitationFilterDto();
        filters.setStatus(REJECTED);

        GoalInvitation invitation1 = Mockito.mock(GoalInvitation.class);
        GoalInvitation invitation2 = Mockito.mock(GoalInvitation.class);

        when(invitation1.getStatus()).thenReturn(PENDING);
        when(invitation2.getStatus()).thenReturn(ACCEPTED);

        Stream<GoalInvitation> goalInvitations = Stream.of(invitation1, invitation2);

        // Act
        Stream<GoalInvitation> result = filter.apply(goalInvitations, filters);

        // Assert
        List<GoalInvitation> resultList = result.toList();
        assertEquals(0, resultList.size());
    }
}
