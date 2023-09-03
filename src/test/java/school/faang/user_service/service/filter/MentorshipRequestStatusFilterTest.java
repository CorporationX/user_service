package school.faang.user_service.service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.mentorship.filter.MentorshipRequestStatusFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MentorshipRequestStatusFilterTest {
    private final MentorshipRequestStatusFilter descriptionFilter = new MentorshipRequestStatusFilter();
    private RequestFilterDto filter;
    private List<MentorshipRequest> requests;
    private MentorshipRequest request1;
    private MentorshipRequest request2;
    private MentorshipRequest request3;
    private User user1;
    private User user2;
    private User user3;


    @BeforeEach
    void setUp() {
        filter = RequestFilterDto.builder().build();

        request1 = new MentorshipRequest();
        request2 = new MentorshipRequest();
        request3 = new MentorshipRequest();

        request1.setStatus(RequestStatus.PENDING);
        request2.setStatus(RequestStatus.ACCEPTED);
        request3.setStatus(RequestStatus.REJECTED);

        requests = new ArrayList<>();

        requests.add(request1);
        requests.add(request2);
        requests.add(request3);
    }

    @Test
    void testIsApplicableWhenReceiverNamePatternNotEmpty() {
        filter.setRequestStatusPattern(RequestStatus.PENDING);
        assertTrue(descriptionFilter.isApplicable(filter));
    }

    @Test
    void testNotApplicableWhenDescriptionPatternEmpty() {
        assertFalse(descriptionFilter.isApplicable(filter));
    }

    @Test
    void testFilterFilters() {
        filter.setRequestStatusPattern(RequestStatus.ACCEPTED);
        Stream<MentorshipRequest> filteredRequestsStream = descriptionFilter.apply(requests.stream(), filter);
        MentorshipRequest filteredRequest = filteredRequestsStream.findFirst().get();

        assertEquals(filteredRequest, request2);
    }
}
