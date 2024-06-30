package school.faang.user_service.service.mentorship.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class StatusFilterTest {

    @InjectMocks
    private StatusFilter statusFilter;

    private RequestFilterDto requestFilterDto;

    private MentorshipRequest mentorshipRequest;

    @BeforeEach
    void prepareRequestFilterDto() {
        requestFilterDto = new RequestFilterDto();
        requestFilterDto.setStatus(RequestStatus.PENDING);
        mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setStatus(RequestStatus.PENDING);
    }

    @Test
    void testIsApplicableFalse() {
        requestFilterDto.setStatus(null);
        assertFalse(statusFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testIsApplicableTrue() {
        assertTrue(statusFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testApplyWithRequest() {
        assertEquals(1, statusFilter.apply(mentorshipRequest, requestFilterDto).toList().size());
    }

    @Test
    void testApplyWithoutRequest() {
        requestFilterDto.setStatus(RequestStatus.REJECTED);
        assertEquals(0, statusFilter.apply(mentorshipRequest, requestFilterDto).toList().size());
    }
}
