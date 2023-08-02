package school.faang.user_service.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filter.InvitationFilter;
import school.faang.user_service.filter.InvitationInvitedIdFilter;
import school.faang.user_service.filter.InvitationInvitedNameFilter;
import school.faang.user_service.filter.InvitationInviterIdFilter;
import school.faang.user_service.filter.InvitationInviterNameFilter;
import school.faang.user_service.filter.InvitationStatusFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InvitationFilterTest {
    @ParameterizedTest
    @MethodSource("argsProvider1")
    public void testIsApplicable(InvitationFilter invitationFilter) {
        InvitationFilterDto filter = new InvitationFilterDto("1", "1", 1L, 1L, RequestStatus.ACCEPTED);

        boolean result1 = invitationFilter.isApplicable(filter);
        boolean result2 = invitationFilter.isApplicable(new InvitationFilterDto());

        assertTrue(result1);
        assertFalse(result2);
    }

    @ParameterizedTest
    @MethodSource("argsProvider2")
    public void testApply(InvitationFilter invitationFilter, InvitationFilterDto filter) {
        User user = User.builder().username("a").id(1).build();
        GoalInvitation invitation = new GoalInvitation();
        invitation.setInviter(user);
        invitation.setInvited(user);
        invitation.setStatus(RequestStatus.ACCEPTED);

        List<GoalInvitation> result = invitationFilter.apply(Stream.of(invitation), filter);

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(invitation, result.get(0))
        );
    }

    static Stream<InvitationFilter> argsProvider1() {
        return Stream.of(
                new InvitationInviterNameFilter(),
                new InvitationInviterIdFilter(),
                new InvitationInvitedNameFilter(),
                new InvitationInvitedIdFilter(),
                new InvitationStatusFilter()
        );
    }

    static Stream<Arguments> argsProvider2() {
        return Stream.of(
                Arguments.of(new InvitationInviterNameFilter(), new InvitationFilterDto("a", null, null, null, null)),
                Arguments.of(new InvitationInviterIdFilter(), new InvitationFilterDto(null, null, 1L, null, null)),
                Arguments.of(new InvitationInvitedNameFilter(), new InvitationFilterDto(null, "a", null, null, null)),
                Arguments.of(new InvitationInvitedIdFilter(), new InvitationFilterDto(null, null, null, 1L, null)),
                Arguments.of(new InvitationStatusFilter(),
                        new InvitationFilterDto(null, null, null, null, RequestStatus.ACCEPTED))
        );
    }
}