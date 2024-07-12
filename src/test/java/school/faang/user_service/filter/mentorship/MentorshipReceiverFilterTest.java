package school.faang.user_service.filter.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MentorshipReceiverFilterTest {

    @InjectMocks
    private MentorshipReceiverFilter mentorshipReceiverFilter;

    @Test
    void testIsApplicable_should_return_true_with_receiver_pattern() {
        var requestFilterDto = new RequestFilterDto();
        requestFilterDto.setReceiverId(1L);
        assertTrue(mentorshipReceiverFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testIsApplicable_should_return_false_without_any_pattern() {
        var requestFilterDto = new RequestFilterDto();
        assertFalse(mentorshipReceiverFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testApply_should_filter_if_receiver_id_matches() {
        MentorshipRequest req1 = new MentorshipRequest();
        var user1 = new User();
        user1.setId(1L);
        req1.setReceiver(user1);
        MentorshipRequest req2 = new MentorshipRequest();
        var user2 = new User();
        user2.setId(2L);
        req2.setReceiver(user2);
        MentorshipRequest req3 = new MentorshipRequest();
        req3.setReceiver(user2);
        Stream<MentorshipRequest> requests = Stream.of(req1, req2, req3);
        var requestFilterDto = new RequestFilterDto();
        requestFilterDto.setReceiverId(2L);

        List<MentorshipRequest> filteredRequests = mentorshipReceiverFilter
                .apply(requests, requestFilterDto)
                .toList();

        assertEquals(2, filteredRequests.size());
        assertFalse(filteredRequests.contains(req1));
        assertTrue(filteredRequests.contains(req2));
        assertTrue(filteredRequests.contains(req3));
    }
}