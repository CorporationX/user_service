package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.mentorship.MentorshipRequestReceiverIdFilter;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MentorshipRequestReceiverIdFilterTest {
    private MentorshipRequestReceiverIdFilter filter;
    private RequestFilterDto requestFilterDto;

    @BeforeEach
    void setUp() {
        filter = new MentorshipRequestReceiverIdFilter();
        requestFilterDto = new RequestFilterDto();
    }

    @Test
    void testIsApplicableTrue() {
        requestFilterDto.setReceiverId(1L);

        assertTrue(filter.isApplicable(requestFilterDto));
    }

    @Test
    void testIsApplicableFalseWhenReceiverIdIsNull() {
        assertFalse(filter.isApplicable(requestFilterDto));
    }

    @Test
    void testIsApplicableFalseWhenReceiverIdLessThanOne() {
        requestFilterDto.setReceiverId(0L);
        assertFalse(filter.isApplicable(requestFilterDto));
    }

    @Test
    void testApplyReturnsOnlyAcceptedRequestsWhenFilteredByStatus() {
        requestFilterDto.setReceiverId(1L);

        User receiverFirst = new User();
        receiverFirst.setId(1L);
        User receiverSecond = new User();
        receiverSecond.setId(2L);

        MentorshipRequest mentorshipRequestFirst = new MentorshipRequest();
        mentorshipRequestFirst.setReceiver(receiverFirst);
        MentorshipRequest mentorshipRequestSecond = new MentorshipRequest();
        mentorshipRequestSecond.setReceiver(receiverSecond);

        Stream<MentorshipRequest> mentorshipRequestStream = Stream.of(mentorshipRequestFirst, mentorshipRequestSecond);

        Stream<MentorshipRequest> mentorshipRequestStreamResult = filter
                .apply(mentorshipRequestStream, requestFilterDto);

        assertEquals(
                receiverFirst.getId(),
                mentorshipRequestStreamResult.findFirst().get().getReceiver().getId());
    }
}