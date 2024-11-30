package school.faang.user_service.service.service.filter.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.service.filter.goal.InvitationInviterIdFilter;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InvitationInviterIdFIlterTest {
    @InjectMocks
    private InvitationInviterIdFilter inviterIdFilter;
    private InvitationFilterDto filterDto;

    @BeforeEach
    void setUp() {
        filterDto = new InvitationFilterDto();
    }

    @Test
    void testIsApplicableSuccess() {
        filterDto.setInviterId(1L);

        assertTrue(inviterIdFilter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicableWithNull() {
        filterDto.setInvitedId(null);

        assertFalse(inviterIdFilter.isApplicable(filterDto));
    }

    @Test
    void testApplySuccess() {
        GoalInvitation goalInvitation1 = mock(GoalInvitation.class);
        GoalInvitation goalInvitation2 = mock(GoalInvitation.class);
        User user1 = mock(User.class);
        User user2 = mock(User.class);

        when(user1.getId()).thenReturn(2L);
        when(user2.getId()).thenReturn(3L);
        when(goalInvitation1.getInviter()).thenReturn(user1);
        when(goalInvitation2.getInviter()).thenReturn(user2);

        Stream<GoalInvitation> invitations = Stream.of(goalInvitation1, goalInvitation2);
        filterDto.setInviterId(2L);

        Stream<GoalInvitation> result = inviterIdFilter.apply(invitations, filterDto);

        assertEquals(1, result.count());
    }

    @Test
    void testApplyWithNoMatchesInvitedId() {
        GoalInvitation goalInvitation1 = mock(GoalInvitation.class);
        GoalInvitation goalInvitation2 = mock(GoalInvitation.class);
        User user1 = mock(User.class);
        User user2 = mock(User.class);

        when(user1.getId()).thenReturn(2L);
        when(user2.getId()).thenReturn(3L);
        when(goalInvitation1.getInviter()).thenReturn(user1);
        when(goalInvitation2.getInviter()).thenReturn(user2);

        Stream<GoalInvitation> invitations = Stream.of(goalInvitation1, goalInvitation2);
        filterDto.setInviterId(4L);

        Stream<GoalInvitation> result = inviterIdFilter.apply(invitations, filterDto);

        assertEquals(0, result.count());
    }

    @Test
    void testApplyWithEmptyStream() {
        Stream<GoalInvitation> invitations = Stream.empty();
        filterDto.setInvitedId(4L);

        Stream<GoalInvitation> result = inviterIdFilter.apply(invitations, filterDto);

        assertEquals(0, result.count());
    }
}
