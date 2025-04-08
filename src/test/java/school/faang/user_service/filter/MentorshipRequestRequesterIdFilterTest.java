package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.mentorship.MentorshipRequestRequesterIdFilter;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MentorshipRequestRequesterIdFilterTest {
    private MentorshipRequestRequesterIdFilter filter;
    private RequestFilterDto requestFilterDto;

    @BeforeEach
    void setUp() {
        filter = new MentorshipRequestRequesterIdFilter();
        requestFilterDto = new RequestFilterDto();
    }

    @Test
    void testIsApplicableTrue() {
        requestFilterDto.setRequesterId(1L);

        assertTrue(filter.isApplicable(requestFilterDto));
    }

    @Test
    void testIsApplicableFalseWhenRequesterIdIsNull() {
        assertFalse(filter.isApplicable(requestFilterDto));
    }

    @Test
    void testIsApplicableFalseWhenRequesterIdLessThenOne() {
        requestFilterDto.setRequesterId(0L);

        assertFalse(filter.isApplicable(requestFilterDto));
    }

    @Test
    void testApplyReturnsOnlyAcceptedRequestsWhenFilteredByStatus() {
        requestFilterDto.setRequesterId(1L);

        User requesterFirst = new User();
        requesterFirst.setId(1L);
        User requesterSecond = new User();
        requesterSecond.setId(2L);

        MentorshipRequest mentorshipRequestFirst = new MentorshipRequest();
        mentorshipRequestFirst.setRequester(requesterFirst);
        MentorshipRequest mentorshipRequestSecond = new MentorshipRequest();
        mentorshipRequestSecond.setRequester(requesterSecond);

        Stream<MentorshipRequest> mentorshipRequestStream = Stream.of(mentorshipRequestFirst, mentorshipRequestSecond);

        Stream<MentorshipRequest> mentorshipRequestStreamResult = filter
                .apply(mentorshipRequestStream, requestFilterDto);

        assertEquals(
                requesterFirst.getId(),
                mentorshipRequestStreamResult.findFirst().get().getRequester().getId());
    }
}