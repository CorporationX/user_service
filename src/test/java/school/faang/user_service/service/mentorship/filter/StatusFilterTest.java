package school.faang.user_service.service.mentorship.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Nested
    class PositiveTests {

        @DisplayName("should return true when status in requestFilerDto != null")
        @Test
        void applicableTest() {
            assertTrue(statusFilter.isApplicable(requestFilterDto));
        }

        @DisplayName("should return 1 when status has the same value in both parameters of apply method")
        @Test
        void applyTest() {
            assertEquals(1, statusFilter.apply(mentorshipRequest, requestFilterDto).toList().size());
        }
    }

    @Nested
    class NegativeTests {

        @DisplayName("should return false when status in requestFilerDto == null")
        @Test
        void applicableTest() {
            requestFilterDto.setStatus(null);
            assertFalse(statusFilter.isApplicable(requestFilterDto));
        }

        @DisplayName("should return 0 when status hasn't the same value in both parameters of apply method")
        @Test
        void applyTest() {
            requestFilterDto.setStatus(RequestStatus.REJECTED);
            assertEquals(0, statusFilter.apply(mentorshipRequest, requestFilterDto).toList().size());
        }
    }
}
