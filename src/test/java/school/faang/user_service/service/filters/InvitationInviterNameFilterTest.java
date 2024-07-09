package school.faang.user_service.service.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filters.InvitationInviterNameFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class InvitationInviterNameFilterTest {

    private InvitationInviterNameFilter filter;
    private User user1;
    private User user2;
    private User admin;


    @BeforeEach
    public void setUp() {
        filter = new InvitationInviterNameFilter();

        user1 = User.builder()
                .id(1L)
                .username("User1")
                .build();

        user2 = User.builder()
                .id(2L)
                .username("User2")
                .build();

        admin = User.builder()
                .id(3L)
                .username("admin")
                .build();
    }

    @Test
    public void testIsAcceptable_withEmptyPattern() {
        // Arrange
        InvitationFilterDto filters = new InvitationFilterDto();
        filters.setInviterNamePattern("");

        // Act
        boolean result = filter.isAcceptable(filters);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testIsAcceptable_withBlankPattern() {
        // Arrange
        InvitationFilterDto filters = new InvitationFilterDto();
        filters.setInviterNamePattern("   ");

        // Act
        boolean result = filter.isAcceptable(filters);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testApply_withMatchingInviterName() {
        // Arrange
        InvitationFilterDto filters = new InvitationFilterDto();
        filters.setInviterNamePattern("User.*");

        GoalInvitation invitation1 = Mockito.mock(GoalInvitation.class);
        GoalInvitation invitation2 = Mockito.mock(GoalInvitation.class);
        GoalInvitation invitation3 = Mockito.mock(GoalInvitation.class);

        when(invitation1.getInviter()).thenReturn(user1);
        when(invitation2.getInviter()).thenReturn(user2);
        when(invitation3.getInviter()).thenReturn(admin);

        Stream<GoalInvitation> goalInvitations = Stream.of(invitation1, invitation2, invitation3);

        // Act
        Stream<GoalInvitation> result = filter.apply(goalInvitations, filters);

        // Assert
        List<GoalInvitation> resultList = result.toList();
        assertEquals(2, resultList.size());
        assertTrue(resultList.contains(invitation1));
        assertTrue(resultList.contains(invitation2));
    }

    @Test
    public void testApply_withNoMatchingInviterName() {
        // Arrange
        InvitationFilterDto filters = new InvitationFilterDto();
        filters.setInviterNamePattern("Admin.*");

        GoalInvitation invitation1 = Mockito.mock(GoalInvitation.class);
        GoalInvitation invitation2 = Mockito.mock(GoalInvitation.class);

        when(invitation1.getInviter()).thenReturn(user1);
        when(invitation2.getInviter()).thenReturn(user2);

        Stream<GoalInvitation> goalInvitations = Stream.of(invitation1, invitation2);

        // Act
        Stream<GoalInvitation> result = filter.apply(goalInvitations, filters);

        // Assert
        List<GoalInvitation> resultList = result.toList();
        assertEquals(0, resultList.size());
    }
}
