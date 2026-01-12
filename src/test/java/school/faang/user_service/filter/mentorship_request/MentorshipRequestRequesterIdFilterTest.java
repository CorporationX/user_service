package school.faang.user_service.filter.mentorship_request;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.entity.user.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestRequesterIdFilterTest {
    private final MentorshipRequestRequesterIdFilter requesterIdFilter = new MentorshipRequestRequesterIdFilter();

    @Test
    void testIsApplicableTrue() {
        boolean result = requesterIdFilter.isApplicable(
                new MentorshipRequestFilterDto(1L, null, null));

        assertTrue(result);
    }

    @Test
    void testIsApplicableFalse() {
        boolean result = requesterIdFilter.isApplicable(
                new MentorshipRequestFilterDto(null, null, null));

        assertFalse(result);
    }

    @Test
    void testApply() {
        MentorshipRequest mentorshipRequest1 = createMentorshipRequest(1L);
        MentorshipRequest mentorshipRequest2 = createMentorshipRequest(2L);
        Stream<MentorshipRequest> mentorshipRequests = Stream.of(
                mentorshipRequest1, mentorshipRequest2
        );

        Stream<MentorshipRequest> mentorshipRequest = requesterIdFilter.apply(mentorshipRequests,
                new MentorshipRequestFilterDto(1L, null, null));

        List<MentorshipRequest> mentorshipRequestsList = mentorshipRequest.toList();
        assertEquals(1, mentorshipRequestsList.size());
        assertEquals(1L, mentorshipRequestsList.get(0).getRequester().getId());
    }

    @Test
    void testApplyNoSuitable() {
        MentorshipRequest mentorshipRequest1 = createMentorshipRequest(1L);
        MentorshipRequest mentorshipRequest2 = createMentorshipRequest(2L);
        Stream<MentorshipRequest> mentorshipRequests = Stream.of(
                mentorshipRequest1, mentorshipRequest2
        );

        Stream<MentorshipRequest> mentorshipRequest = requesterIdFilter.apply(mentorshipRequests,
                new MentorshipRequestFilterDto(0L, null, null));

        List<MentorshipRequest> mentorshipRequestsList = mentorshipRequest.toList();
        assertEquals(0, mentorshipRequestsList.size());
    }

    @NotNull
    private MentorshipRequest createMentorshipRequest(long requesterId) {
        User user1 = new User();
        user1.setId(requesterId);
        MentorshipRequest mentorshipRequest1 = new MentorshipRequest();
        mentorshipRequest1.setRequester(user1);

        return mentorshipRequest1;
    }
}
