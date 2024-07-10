package school.faang.user_service.mentorship_request.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.ReceiverFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ReceiverFilterTest {

    private final ReceiverFilter receiverFilter = new ReceiverFilter();
    private final RequestFilterDto filterDto = new RequestFilterDto();
    private List<MentorshipRequest> requests = new ArrayList<>();

    @BeforeEach
    public void prepareRequests() {
        MentorshipRequest firstRequest = new MentorshipRequest();
        User firstReceiver = new User();
        firstReceiver.setId(1L);
        firstRequest.setReceiver(firstReceiver);
        MentorshipRequest secondRequest = new MentorshipRequest();
        User secondReceiver = new User();
        secondReceiver.setId(2L);
        secondRequest.setReceiver(secondReceiver);
        MentorshipRequest thirdRequest = new MentorshipRequest();
        User thirdReceiver = new User();
        thirdReceiver.setId(3L);
        thirdRequest.setReceiver(thirdReceiver);
        requests = List.of(firstRequest, secondRequest, thirdRequest);
    }

    @Test
    public void testReceiverFilterIsApplicable() {
        filterDto.setReceiverId(3L);

        boolean isApplicable = receiverFilter.isApplicable(filterDto);

        assertTrue(isApplicable);
    }

    @Test
    public void testReceiverFilterDoesNotIsApplicable() {

        boolean isApplicable = receiverFilter.isApplicable(filterDto);

        assertFalse(isApplicable);
    }

    @Test
    public void testReceiverFilterApplied() {
        filterDto.setReceiverId(3L);
        User receiver = new User();
        receiver.setId(3L);
        MentorshipRequest firstRequest = new MentorshipRequest();
        firstRequest.setReceiver(receiver);
        List<MentorshipRequest> testResultRequests = List.of(firstRequest);

        Stream<MentorshipRequest> filterResult = receiverFilter.apply(requests.stream(), filterDto);
        assertEquals(filterResult.toList(), testResultRequests);
    }
}
