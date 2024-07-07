package school.faang.user_service.controller.mentorship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestControllerTest {

    @InjectMocks
    private MentorshipRequestController mentorshipRequestController;

    @Mock
    private MentorshipRequestService mentorshipRequestService;

    @Captor
    private ArgumentCaptor<MentorshipRequestDto> requestDtoArgumentCaptor;

    @Captor
    private ArgumentCaptor<Long> longArgumentCaptor;

    @Captor
    private ArgumentCaptor<RequestFilterDto> requestFilterDtoArgumentCaptor;

    @Captor
    private ArgumentCaptor<RejectionDto> rejectionDtoArgumentCaptor;

    private static final long ID = 1L;

    @Nested
    class PositiveTests {

        @DisplayName("should return mentorshipRequestDto with PENDING when passed")
        @Test
        void requestMentorshipTest() {
            MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();
            mentorshipRequestDto.setStatus(RequestStatus.PENDING);

            mentorshipRequestController.requestMentorship(mentorshipRequestDto);

            verify(mentorshipRequestService).requestMentorship(requestDtoArgumentCaptor.capture());
            assertEquals(mentorshipRequestDto.getStatus(), requestDtoArgumentCaptor.getValue().getStatus());
        }

        @DisplayName("should return ID with 1L when passed")
        @Test
        void acceptRequestTest() {
            mentorshipRequestController.acceptRequest(ID);

            verify(mentorshipRequestService).acceptRequest(longArgumentCaptor.capture());

            assertEquals(ID, longArgumentCaptor.getValue());
        }

        @DisplayName("should return ID with 1L & rejection with Some reason when passed")
        @Test
        void rejectRequestTest() {
            RejectionDto rejection = new RejectionDto();
            rejection.setRejectionReason("Some reason");
            mentorshipRequestController.rejectRequest(ID, rejection);

            verify(mentorshipRequestService).rejectRequest(longArgumentCaptor.capture(), rejectionDtoArgumentCaptor.capture());

            assertEquals(ID, longArgumentCaptor.getValue());
            assertEquals(rejection, rejectionDtoArgumentCaptor.getValue());
        }

        @DisplayName("should return requestFilterDto with ACCEPTED when passed")
        @Test
        void findAllTest() {
            RequestFilterDto requestFilterDto = new RequestFilterDto();
            requestFilterDto.setStatus(RequestStatus.ACCEPTED);

            mentorshipRequestController.findAll(requestFilterDto);

            verify(mentorshipRequestService).findAll(requestFilterDtoArgumentCaptor.capture());
            assertEquals(requestFilterDto.getStatus(), requestFilterDtoArgumentCaptor.getValue().getStatus());
        }
    }
}
