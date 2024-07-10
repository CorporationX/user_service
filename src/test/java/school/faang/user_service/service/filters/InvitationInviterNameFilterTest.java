package school.faang.user_service.service.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Test isAcceptable method with empty pattern")
    public void testIsAcceptable_withEmptyPattern() {
        InvitationFilterDto filters = new InvitationFilterDto();
        filters.setInviterNamePattern("");

        boolean result = filter.isAcceptable(filters);

        assertFalse(result);
    }

    @Test
    @DisplayName("Test isAcceptable method with blank pattern")
    public void testIsAcceptable_withBlankPattern() {
        InvitationFilterDto filters = new InvitationFilterDto();
        filters.setInviterNamePattern("   ");

        boolean result = filter.isAcceptable(filters);

        assertFalse(result);
    }

    @Test
    @DisplayName("Test apply method with matching inviter name")
    public void testApply_withMatchingInviterName() {
        InvitationFilterDto filters = new InvitationFilterDto();
        filters.setInviterNamePattern("User.*");

        GoalInvitation invitation1 = Mockito.mock(GoalInvitation.class);
        GoalInvitation invitation2 = Mockito.mock(GoalInvitation.class);
        GoalInvitation invitation3 = Mockito.mock(GoalInvitation.class);

        when(invitation1.getInviter()).thenReturn(user1);
        when(invitation2.getInviter()).thenReturn(user2);
        when(invitation3.getInviter()).thenReturn(admin);

        Stream<GoalInvitation> goalInvitations = Stream.of(invitation1, invitation2, invitation3);

        Stream<GoalInvitation> result = filter.apply(goalInvitations, filters);

        List<GoalInvitation> resultList = result.toList();
        assertEquals(2, resultList.size());
        assertTrue(resultList.contains(invitation1));
        assertTrue(resultList.contains(invitation2));
    }

    @Test
    @DisplayName("Test apply method with no matching inviter name")
    public void testApply_withNoMatchingInviterName() {
        InvitationFilterDto filters = new InvitationFilterDto();
        filters.setInviterNamePattern("Admin.*");

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
