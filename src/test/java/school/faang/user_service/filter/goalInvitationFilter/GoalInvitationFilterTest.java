package school.faang.user_service.filter.goalInvitationFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filter.goalInvitationFilters.*;

import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GoalInvitationFilterTest {

    private GoalInvitationFilter filter;
    private GoalInvitation firstInvitation;
    private GoalInvitation secondInvitation;

    @BeforeEach
    public void setUp() {
        firstInvitation = new GoalInvitation();
        secondInvitation = new GoalInvitation();
    }

    @Test
    public void testInvitedIdFilter() {
        filter = new InvitedIdFilter();
        User firstInvited = new User();
        firstInvited.setId(1L);
        User secondInvited = new User();
        secondInvited.setId(2L);
        firstInvitation.setInvited(firstInvited);
        secondInvitation.setInvited(secondInvited);
        InvitationFilterDto filterDto = InvitationFilterDto.builder()
                .invitedId(1L)
                .build();

        assertTrue(filter.isApplicable(filterDto));
        Stream<GoalInvitation> stream = filter.apply(Stream.of(firstInvitation, secondInvitation), filterDto);
        assertTrue(stream.allMatch(inv -> Objects.equals(inv.getInvited().getId(), filterDto.invitedId())));
    }

    @Test
    public void testInviterIdFilter() {
        filter = new InviterIdFilter();
        User firstInviter = new User();
        firstInviter.setId(1L);
        User secondInviter = new User();
        secondInviter.setId(2L);
        firstInvitation.setInviter(firstInviter);
        secondInvitation.setInviter(secondInviter);
        InvitationFilterDto filterDto = InvitationFilterDto.builder()
                .inviterId(1L)
                .build();

        assertTrue(filter.isApplicable(filterDto));
        Stream<GoalInvitation> stream = filter.apply(Stream.of(firstInvitation, secondInvitation), filterDto);
        assertTrue(stream.allMatch(inv -> Objects.equals(inv.getInviter().getId(), filterDto.inviterId())));
    }

    @Test
    public void testRequestStatusFilter() {
        filter = new RequestStatusFilter();
        firstInvitation.setStatus(RequestStatus.PENDING);
        secondInvitation.setStatus(RequestStatus.ACCEPTED);
        InvitationFilterDto filterDto = InvitationFilterDto.builder()
                .status(RequestStatus.PENDING)
                .build();

        assertTrue(filter.isApplicable(filterDto));
        Stream<GoalInvitation> stream = filter.apply(Stream.of(firstInvitation, secondInvitation), filterDto);
        assertTrue(stream.allMatch(inv -> Objects.equals(inv.getStatus(), filterDto.status())));
    }

    @Test
    public void testInvitedNamePatternFilter() {
        filter = new InvitedNamePatternFilter();
        User firstInvited = new User();
        firstInvited.setUsername("John");
        User secondInvited = new User();
        secondInvited.setUsername("Jack");
        firstInvitation.setInvited(firstInvited);
        secondInvitation.setInvited(secondInvited);
        InvitationFilterDto filterDto = InvitationFilterDto.builder()
                .invitedNamePattern("John")
                .build();

        assertTrue(filter.isApplicable(filterDto));
        Stream<GoalInvitation> stream = filter.apply(Stream.of(firstInvitation, secondInvitation), filterDto);
        assertTrue(stream.allMatch(inv -> Objects.equals(inv.getInvited().getUsername(), filterDto.invitedNamePattern())));
    }
}
