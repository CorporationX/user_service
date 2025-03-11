package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MentorshipRequestStatusFilterTest {
    private MentorshipRequestStatusFilter filter;
    private RequestFilterDto requestFilterDto;

    @BeforeEach
    void setUp() {
        filter = new MentorshipRequestStatusFilter();
        requestFilterDto = new RequestFilterDto();
    }

    @Test
    void testIsApplicableTrue() {
        requestFilterDto.setStatus(RequestStatus.ACCEPTED);

        assertTrue(filter.isApplicable(requestFilterDto));
    }

    @Test
    void testIsApplicableFalseWhenStatusIsNull() {
        assertFalse(filter.isApplicable(requestFilterDto));
    }

    @Test
    void testApplyReturnsOnlyAcceptedRequestsWhenFilteredByStatus() {
        requestFilterDto.setStatus(RequestStatus.ACCEPTED);

        MentorshipRequest mentorshipRequestFirst = new MentorshipRequest();
        mentorshipRequestFirst.setStatus(RequestStatus.ACCEPTED);

        MentorshipRequest mentorshipRequestSecond = new MentorshipRequest();
        mentorshipRequestSecond.setStatus(RequestStatus.REJECTED);

        Stream<MentorshipRequest> mentorshipRequestStream = Stream.of(
                mentorshipRequestFirst,
                mentorshipRequestSecond
        );

        Stream<MentorshipRequest> mentorshipRequestStreamResult = filter.apply(
                mentorshipRequestStream,
                requestFilterDto);

        assertEquals(
                mentorshipRequestFirst.getStatus(),
                mentorshipRequestStreamResult.findFirst().get().getStatus());
    }
}