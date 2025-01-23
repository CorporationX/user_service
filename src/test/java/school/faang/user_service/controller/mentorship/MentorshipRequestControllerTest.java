package school.faang.user_service.controller.mentorship;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRejectionDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.mentorship.MentorshipRequestService;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestControllerTest {
    @Mock
    private MentorshipRequestService mentorshipRequestService;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MentorshipRequestController mentorshipRequestController;

    private static final Long REQUESTER_ID = 1L;
    private static final Long RECEIVER_ID = 2L;
    private static final Long ACCEPTED_ID = 1L;
    private static final String DESCRIPTION = "I want you to be my mentor";
    private static final String REASON = "I'm very busy now. Sorry. Find another mentor.";
    private static final RequestStatus REQUEST_STATUS = RequestStatus.PENDING;

    @Test
    public void testDtoIsNull() {
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();

        Assert.assertThrows(
                DataValidationException.class,
                () -> mentorshipRequestController.requestMentorship(mentorshipRequestDto));
    }

    @Test
    public void testDescriptionIsEmpty() {
        MentorshipRequestDto mentorshipRequestDto = prepareDataToCreateRequestDto(REQUESTER_ID,
                RECEIVER_ID,
                "",
                REQUEST_STATUS);

        Assert.assertThrows(
                DataValidationException.class,
                () -> mentorshipRequestController.requestMentorship(mentorshipRequestDto));
    }

    @Test
    public void testMentorshipRequestCreate() {
        MentorshipRequestDto mentorshipRequestDto = prepareDataToCreateRequestDto(
                REQUESTER_ID,
                RECEIVER_ID,
                DESCRIPTION,
                REQUEST_STATUS);
        mentorshipRequestController.requestMentorship(mentorshipRequestDto);
        verify(mentorshipRequestService, Mockito.times(1))
                .requestMentorship(mentorshipRequestDto);
    }

    @Test
    public void testAcceptRequest() {
        mentorshipRequestController.acceptRequest(ACCEPTED_ID);
        verify(mentorshipRequestService, Mockito.times(1))
                .acceptRequest(ACCEPTED_ID);
    }

    @Test
    public void testRejectRequestIsNull() {
        MentorshipRejectionDto mentorshipRejectionDto = new MentorshipRejectionDto();

        Assert.assertThrows(
                DataValidationException.class,
                () -> mentorshipRequestController.rejectRequest(mentorshipRejectionDto));
    }

    @Test
    public void testRejectRequest() {
        MentorshipRejectionDto mentorshipRejectionDto = prepareDataToCreateRejectionDto();
        mentorshipRequestController.rejectRequest(mentorshipRejectionDto);
        verify(mentorshipRequestService, Mockito.times(1))
                .rejectRequest(mentorshipRejectionDto);
    }

    @Test
    public void testGetRequests() {
        MentorshipRequestFilterDto filters = prepareDataToGetRequestsDto();
        mentorshipRequestController.getRequests(filters);
        verify(mentorshipRequestService, Mockito.times(1))
                .getRequests(filters);
    }

    private MentorshipRequestFilterDto prepareDataToGetRequestsDto() {
        MentorshipRequestFilterDto filters = new MentorshipRequestFilterDto();
        filters.setRequesterId(REQUESTER_ID);
        return filters;
    }

    private MentorshipRejectionDto prepareDataToCreateRejectionDto() {
        MentorshipRejectionDto mentorshipRejectionDto = new MentorshipRejectionDto();
        mentorshipRejectionDto.setId(1L);
        mentorshipRejectionDto.setRequesterId(REQUESTER_ID);
        mentorshipRejectionDto.setReceiverId(RECEIVER_ID);
        mentorshipRejectionDto.setDescription(DESCRIPTION);
        mentorshipRejectionDto.setReason(REASON);
        return mentorshipRejectionDto;
    }

    private MentorshipRequestDto prepareDataToCreateRequestDto(Long requesterId, Long receiverId, String description, RequestStatus requestStatus) {
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setRequesterId(requesterId);
        mentorshipRequestDto.setReceiverId(receiverId);
        mentorshipRequestDto.setDescription(description);
        mentorshipRequestDto.setStatus(requestStatus);
        return mentorshipRequestDto;
    }
}
