package school.faang.user_service.service.service.filter.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.service.filter.goal.InvitationStatusFilter;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InvitationStatusFilterTest {
    @InjectMocks
    private InvitationStatusFilter statusFilter;
    private InvitationFilterDto filterDto;

    @BeforeEach
    void setUp() {
        filterDto = new InvitationFilterDto();
    }

    @Test
    void testIsApplicableSuccess() {
        filterDto.setStatus(RequestStatus.REJECTED);
        assertTrue(statusFilter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicableWithNull() {
        filterDto.setStatus(null);
        assertFalse(statusFilter.isApplicable(filterDto));
    }

    @Test
    void testApplySuccess() {
        GoalInvitation goalInvitation1 = mock(GoalInvitation.class);
        GoalInvitation goalInvitation2 = mock(GoalInvitation.class);
        when(goalInvitation1.getStatus()).thenReturn(RequestStatus.ACCEPTED);
        when(goalInvitation2.getStatus()).thenReturn(RequestStatus.PENDING);

        Stream<GoalInvitation> invitations = Stream.of(goalInvitation1, goalInvitation2);
        filterDto.setStatus(RequestStatus.ACCEPTED);

        Stream<GoalInvitation> result = statusFilter.apply(invitations, filterDto);

        assertEquals(1, result.count());
    }

    @Test
    void testApplyNoMatches() {
        GoalInvitation goalInvitation1 = mock(GoalInvitation.class);
        GoalInvitation goalInvitation2 = mock(GoalInvitation.class);
        when(goalInvitation1.getStatus()).thenReturn(RequestStatus.ACCEPTED);
        when(goalInvitation2.getStatus()).thenReturn(RequestStatus.PENDING);

        Stream<GoalInvitation> invitations = Stream.of(goalInvitation1, goalInvitation2);
        filterDto.setStatus(RequestStatus.REJECTED);

        Stream<GoalInvitation> result = statusFilter.apply(invitations, filterDto);

        assertEquals(0, result.count());
    }

    @Test
    void testApplyWithEmptyStream() {
        Stream<GoalInvitation> invitations = Stream.empty();
        filterDto.setStatus(RequestStatus.REJECTED);

        Stream<GoalInvitation> result = statusFilter.apply(invitations, filterDto);

        assertEquals(0, result.count());
    }
}
