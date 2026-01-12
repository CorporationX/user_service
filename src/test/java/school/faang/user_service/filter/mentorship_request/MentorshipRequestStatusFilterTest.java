package school.faang.user_service.filter.mentorship_request;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.MentorshipRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class  MentorshipRequestStatusFilterTest {
    private final MentorshipRequestStatusFilter statusFilter = new MentorshipRequestStatusFilter();

    @Test
    void testIsApplicableTrue() {
        boolean result = statusFilter.isApplicable(
                new MentorshipRequestFilterDto(null, null, RequestStatus.ACCEPTED));

        assertTrue(result);
    }

    @Test
    void testIsApplicableFalse() {
        boolean result = statusFilter.isApplicable(
                new MentorshipRequestFilterDto(null, null, null));

        assertFalse(result);
    }

    @Test
    void testApply() {
        MentorshipRequest mentorshipRequest1 = createMentorshipRequest(RequestStatus.ACCEPTED);
        MentorshipRequest mentorshipRequest2 = createMentorshipRequest(RequestStatus.REJECTED);
        Stream<MentorshipRequest> mentorshipRequests = Stream.of(
                mentorshipRequest1, mentorshipRequest2
        );

        Stream<MentorshipRequest> mentorshipRequest = statusFilter.apply(mentorshipRequests,
                new MentorshipRequestFilterDto(null, null, RequestStatus.ACCEPTED));

        List<MentorshipRequest> mentorshipRequestsList = mentorshipRequest.toList();
        assertEquals(1, mentorshipRequestsList.size());
        assertEquals(RequestStatus.ACCEPTED, mentorshipRequestsList.get(0).getStatus());
    }

    @Test
    void testApplyNoSuitable() {
        MentorshipRequest mentorshipRequest1 = createMentorshipRequest(RequestStatus.ACCEPTED);
        MentorshipRequest mentorshipRequest2 = createMentorshipRequest(RequestStatus.REJECTED);
        Stream<MentorshipRequest> mentorshipRequests = Stream.of(
                mentorshipRequest1, mentorshipRequest2
        );

        Stream<MentorshipRequest> mentorshipRequest = statusFilter.apply(mentorshipRequests,
                new MentorshipRequestFilterDto(null, null, null));

        List<MentorshipRequest> mentorshipRequestsList = mentorshipRequest.toList();
        assertEquals(0, mentorshipRequestsList.size());
    }

    @NotNull
    private MentorshipRequest createMentorshipRequest(RequestStatus requestStatus) {
        MentorshipRequest mentorshipRequest1 = new MentorshipRequest();
        mentorshipRequest1.setStatus(requestStatus);

        return mentorshipRequest1;
    }
}
