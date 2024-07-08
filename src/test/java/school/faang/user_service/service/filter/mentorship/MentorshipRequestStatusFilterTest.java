package school.faang.user_service.service.filter.mentorship;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;

import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestStatusFilterTest {

    @InjectMocks
    private MentorshipRequestStatusFilter mentorshipRequestStatusFilter;

    @Test
    void testIsApplicable_should_return_true_with_status_pattern() {
        var requestFilterDto = new RequestFilterDto();
        requestFilterDto.setRequestStatusPattern("PENDING");
        assertTrue(mentorshipRequestStatusFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testIsApplicable_should_return_false_without_any_pattern() {
        var requestFilterDto = new RequestFilterDto();
        assertFalse(mentorshipRequestStatusFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testApply_should_filter_if_status_matches() {
        MentorshipRequest req1 = new MentorshipRequest();
        req1.setStatus(RequestStatus.PENDING);
        MentorshipRequest req2 = new MentorshipRequest();
        req2.setStatus(RequestStatus.ACCEPTED);
        MentorshipRequest req3 = new MentorshipRequest();
        req3.setStatus(RequestStatus.PENDING);
        Stream<MentorshipRequest> requests = Stream.of(req1, req2, req3);
        var requestFilterDto = new RequestFilterDto();
        requestFilterDto.setRequestStatusPattern("PENDING");

        List<MentorshipRequest> filteredRequests = mentorshipRequestStatusFilter
                .apply(requests, requestFilterDto)
                .toList();

        assertEquals(2, filteredRequests.size());
        assertTrue(filteredRequests.contains(req1));
        assertFalse(filteredRequests.contains(req2));
        assertTrue(filteredRequests.contains(req3));
    }
}