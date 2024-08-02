package school.faang.user_service.service.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filters.Invitation.InvitationInvitedNameFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class InvitationInvitedNameFilterTest {

    private InvitationInvitedNameFilter filter;
    private User user1;
    private User user2;
    private User admin;


    @BeforeEach
    public void setUp() {
        filter = new InvitationInvitedNameFilter();

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
        filters.setInvitedNamePattern("");

        boolean result = filter.isAcceptable(filters);

        assertFalse(result);
    }

    @Test
    @DisplayName("Test isAcceptable method with blank pattern")
    public void testIsAcceptable_withBlankPattern() {
        InvitationFilterDto filters = new InvitationFilterDto();
        filters.setInvitedNamePattern("   ");

        boolean result = filter.isAcceptable(filters);

        assertFalse(result);
    }

    @Test
    @DisplayName("Test apply method with matching invited name")
    public void testApply_withMatchingInvitedName() {
        InvitationFilterDto filters = new InvitationFilterDto();
        filters.setInvitedNamePattern("User.*");

        GoalInvitation invitation1 = Mockito.mock(GoalInvitation.class);
        GoalInvitation invitation2 = Mockito.mock(GoalInvitation.class);
        GoalInvitation invitation3 = Mockito.mock(GoalInvitation.class);

        when(invitation1.getInvited()).thenReturn(user1);
        when(invitation2.getInvited()).thenReturn(user2);
        when(invitation3.getInvited()).thenReturn(admin);

        Stream<GoalInvitation> goalInvitations = Stream.of(invitation1, invitation2, invitation3);

        Stream<GoalInvitation> result = filter.apply(goalInvitations, filters);

        List<GoalInvitation> resultList = result.toList();
        assertEquals(2, resultList.size());
        assertTrue(resultList.contains(invitation1));
        assertTrue(resultList.contains(invitation2));
    }

    @Test
    @DisplayName("Test apply method with no matching invited name")
    public void testApply_withNoMatchingInvitedName() {
        InvitationFilterDto filters = new InvitationFilterDto();
        filters.setInvitedNamePattern("Admin.*");

        GoalInvitation invitation1 = Mockito.mock(GoalInvitation.class);
        GoalInvitation invitation2 = Mockito.mock(GoalInvitation.class);

        when(invitation1.getInvited()).thenReturn(user1);
        when(invitation2.getInvited()).thenReturn(user2);

        Stream<GoalInvitation> goalInvitations = Stream.of(invitation1, invitation2);

        Stream<GoalInvitation> result = filter.apply(goalInvitations, filters);

        List<GoalInvitation> resultList = result.toList();
        assertEquals(0, resultList.size());
    }
}

