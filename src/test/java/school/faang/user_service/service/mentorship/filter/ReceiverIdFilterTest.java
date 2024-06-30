package school.faang.user_service.service.mentorship.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ReceiverIdFilterTest {

    @InjectMocks
    private ReceiverIdFilter receiverIdFilter;

    private RequestFilterDto requestFilterDto;

    private MentorshipRequest mentorshipRequest;

    @BeforeEach
    void prepareRequestFilterDto() {
        requestFilterDto = new RequestFilterDto();
        requestFilterDto.setReceiverId(1L);
        mentorshipRequest = new MentorshipRequest();
        User user = new User();
        user.setId(1L);
        mentorshipRequest.setReceiver(user);
    }

    @Test
    void testIsApplicableFalse() {
        requestFilterDto.setReceiverId(null);
        assertFalse(receiverIdFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testIsApplicableTrue() {
        assertTrue(receiverIdFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testApplyWithRequest() {
        assertEquals(1, receiverIdFilter.apply(mentorshipRequest, requestFilterDto).toList().size());
    }

    @Test
    void testApplyWithoutRequest() {
        requestFilterDto.setReceiverId(2L);
        assertEquals(0, receiverIdFilter.apply(mentorshipRequest, requestFilterDto).toList().size());
    }
}
