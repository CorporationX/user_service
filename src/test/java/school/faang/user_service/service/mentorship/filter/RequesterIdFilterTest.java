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
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class RequesterIdFilterTest {

    @InjectMocks
    private RequesterIdFilter requesterIdFilter;

    private RequestFilterDto requestFilterDto;

    private MentorshipRequest mentorshipRequest;

    @BeforeEach
    void prepareRequestFilterDto() {
        requestFilterDto = new RequestFilterDto();
        requestFilterDto.setRequesterId(1L);
        mentorshipRequest = new MentorshipRequest();
        User user = new User();
        user.setId(1L);
        mentorshipRequest.setRequester(user);
    }

    @Test
    void testIsApplicableFalse() {
        requestFilterDto.setRequesterId(null);
        assertFalse(requesterIdFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testIsApplicableTrue() {
        assertTrue(requesterIdFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testApplyWithRequest() {
        assertEquals(1, requesterIdFilter.apply(mentorshipRequest, requestFilterDto).toList().size());
    }

    @Test
    void testApplyWithoutRequest() {
        requestFilterDto.setRequesterId(2L);
        assertEquals(0, requesterIdFilter.apply(mentorshipRequest, requestFilterDto).toList().size());
    }
}
