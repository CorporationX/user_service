package school.faang.user_service.service.service.filter.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.service.filter.goal.InvitationInviterNameFilter;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InvitationInviterNameFilterTest {
    @InjectMocks
    private InvitationInviterNameFilter inviterNameFilter;
    private InvitationFilterDto filterDto;

    @BeforeEach
    void setUp() {
        filterDto = new InvitationFilterDto();
    }

    @Test
    void testIsApplicableSuccess() {
        filterDto.setInviterNamePattern("name");
        assertTrue(inviterNameFilter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicableNullPattern() {
        filterDto.setInviterNamePattern(null);
        assertFalse(inviterNameFilter.isApplicable(filterDto));
    }

    @Test
    void testApplySuccess() {
        GoalInvitation goalInvitation1 = mock(GoalInvitation.class);
        GoalInvitation goalInvitation2 = mock(GoalInvitation.class);
        User user1 = mock(User.class);
        User user2 = mock(User.class);

        when(user1.getUsername()).thenReturn("Gadzhi");
        when(user2.getUsername()).thenReturn("Vlad");
        when(goalInvitation1.getInviter()).thenReturn(user1);
        when(goalInvitation2.getInviter()).thenReturn(user2);

        Stream<GoalInvitation> invitations = Stream.of(goalInvitation1, goalInvitation2);
        filterDto.setInviterNamePattern("Gadzhi");

        Stream<GoalInvitation> result = inviterNameFilter.apply(invitations, filterDto);

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
        when(goalInvitation1.getInviter()).thenReturn(user1);
        when(goalInvitation2.getInviter()).thenReturn(user2);

        Stream<GoalInvitation> invitations = Stream.of(goalInvitation1, goalInvitation2);
        filterDto.setInviterNamePattern("Gadzhh");

        Stream<GoalInvitation> result = inviterNameFilter.apply(invitations, filterDto);

        assertEquals(0, result.count());
    }

    @Test
    void testApplyWithEmptyStream() {
        Stream<GoalInvitation> invitations = Stream.empty();
        filterDto.setInviterNamePattern("Gadzhh");

        Stream<GoalInvitation> result = inviterNameFilter.apply(invitations, filterDto);

        assertEquals(0, result.count());
    }
}
