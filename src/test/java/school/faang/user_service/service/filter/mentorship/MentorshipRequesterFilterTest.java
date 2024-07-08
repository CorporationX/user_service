package school.faang.user_service.service.filter.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MentorshipRequesterFilterTest {

    @InjectMocks
    private MentorshipRequesterFilter mentorshipRequesterFilter;

    @Test
    void testIsApplicable_should_return_true_with_requester_pattern() {
        var requestFilterDto = new RequestFilterDto();
        requestFilterDto.setRequesterPattern(1L);
        assertTrue(mentorshipRequesterFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testIsApplicable_should_return_false_without_any_pattern() {
        var requestFilterDto = new RequestFilterDto();
        assertFalse(mentorshipRequesterFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testApply_should_filter_if_receiver_id_matches() {
        MentorshipRequest req1 = new MentorshipRequest();
        var user1 = new User();
        user1.setId(1L);
        req1.setRequester(user1);
        MentorshipRequest req2 = new MentorshipRequest();
        var user2 = new User();
        user2.setId(2L);
        req2.setRequester(user2);
        MentorshipRequest req3 = new MentorshipRequest();
        req3.setRequester(user2);
        Stream<MentorshipRequest> requests = Stream.of(req1, req2, req3);
        var requestFilterDto = new RequestFilterDto();
        requestFilterDto.setRequesterPattern(2L);

        List<MentorshipRequest> filteredRequests = mentorshipRequesterFilter
                .apply(requests, requestFilterDto)
                .toList();

        assertEquals(2, filteredRequests.size());
        assertFalse(filteredRequests.contains(req1));
        assertTrue(filteredRequests.contains(req2));
        assertTrue(filteredRequests.contains(req3));
    }
}