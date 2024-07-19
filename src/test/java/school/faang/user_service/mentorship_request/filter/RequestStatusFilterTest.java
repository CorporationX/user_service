package school.faang.user_service.mentorship_request.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship_request.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.filter.mentorship_request.RequestStatusFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class RequestStatusFilterTest {

    private final RequestStatusFilter requestStatusFilter = new RequestStatusFilter();
    private final RequestFilterDto filterDto = new RequestFilterDto();
    private List<MentorshipRequest> requests = new ArrayList<>();

    @BeforeEach
    public void prepareRequests() {
        MentorshipRequest firstRequest = new MentorshipRequest();
        firstRequest.setStatus(RequestStatus.REJECTED);
        MentorshipRequest secondRequest = new MentorshipRequest();
        secondRequest.setStatus(RequestStatus.ACCEPTED);
        MentorshipRequest thirdRequest = new MentorshipRequest();
        thirdRequest.setStatus(RequestStatus.ACCEPTED);
        requests = List.of(firstRequest, secondRequest, thirdRequest);
    }

    @Test
    public void testRequestStatusFilterIsApplicable() {
        filterDto.setStatus(RequestStatus.REJECTED);

        boolean isApplicable = requestStatusFilter.isApplicable(filterDto);

        assertTrue(isApplicable);
    }

    @Test
    public void testRequestStatusFilterDoesNotIsApplicable() {

        boolean isApplicable = requestStatusFilter.isApplicable(filterDto);

        assertFalse(isApplicable);
    }

    @Test
    public void testRequestStatusFilterApplied() {
        filterDto.setStatus(RequestStatus.ACCEPTED);
        MentorshipRequest firstRequest = new MentorshipRequest();
        firstRequest.setStatus(RequestStatus.ACCEPTED);
        MentorshipRequest secondRequest = new MentorshipRequest();
        secondRequest.setStatus(RequestStatus.ACCEPTED);
        List<MentorshipRequest> testResultRequests = List.of(firstRequest, secondRequest);

        Stream<MentorshipRequest> filterResult = requestStatusFilter.apply(requests.stream(), filterDto);
        assertEquals(filterResult.toList(), testResultRequests);
    }
}
