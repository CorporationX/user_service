package school.faang.user_service.service.service.filter.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.service.filter.goal.InvitationInvitedNameFilter;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InvitationInvitedNameFilterTest {
    @InjectMocks
    private InvitationInvitedNameFilter invitedNameFilter;
    private InvitationFilterDto filterDto;

    @BeforeEach
    void setUp() {
        filterDto = new InvitationFilterDto();
    }

    @Test
    void testIsApplicableSuccess() {
        filterDto.setInvitedNamePattern("name");
        assertTrue(invitedNameFilter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicableNullPattern() {
        filterDto.setInvitedNamePattern(null);
        assertFalse(invitedNameFilter.isApplicable(filterDto));
    }

    @Test
    void testApplySuccess() {
        GoalInvitation goalInvitation1 = mock(GoalInvitation.class);
        GoalInvitation goalInvitation2 = mock(GoalInvitation.class);
        User user1 = mock(User.class);
        User user2 = mock(User.class);

        when(user1.getUsername()).thenReturn("Gadzhi");
        when(user2.getUsername()).thenReturn("Vlad");
        when(goalInvitation1.getInvited()).thenReturn(user1);
        when(goalInvitation2.getInvited()).thenReturn(user2);

        Stream<GoalInvitation> invitations = Stream.of(goalInvitation1, goalInvitation2);
        filterDto.setInvitedNamePattern("Gadzhi");

        Stream<GoalInvitation> result = invitedNameFilter.apply(invitations, filterDto);

        assertEquals(1, result.count());
    }

    @Test
    void testApplyNoMatchesInvitedNamePattern() {
        GoalInvitation goalInvitation1 = mock(GoalInvitation.class);
        GoalInvitation goalInvitation2 = mock(GoalInvitation.class);
        User user1 = mock(User.class);
        User user2 = mock(User.class);

        when(user1.getUsername()).thenReturn("Gadzhi");
        when(user2.getUsername()).thenReturn("Vlad");
        when(goalInvitation1.getInvited()).thenReturn(user1);
        when(goalInvitation2.getInvited()).thenReturn(user2);

        Stream<GoalInvitation> invitations = Stream.of(goalInvitation1, goalInvitation2);
        filterDto.setInvitedNamePattern("Gadzhh");

        Stream<GoalInvitation> result = invitedNameFilter.apply(invitations, filterDto);

        assertEquals(0, result.count());
    }

    @Test
    void testApplyWithEmptyStream() {
        Stream<GoalInvitation> invitations = Stream.empty();
        filterDto.setInvitedNamePattern("Gadzhh");

        Stream<GoalInvitation> result = invitedNameFilter.apply(invitations, filterDto);

        assertEquals(0, result.count());
    }
}
