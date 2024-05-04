package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipRequestFilterDto;
import school.faang.user_service.dto.MentorshipResponseDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.filter.MentorshipRequestDescriptionFilter;
import school.faang.user_service.service.filter.MentorshipRequestFilter;
import school.faang.user_service.util.MentorshipValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {

    private static final long REQUESTER_ID = 1L;
    private static final long RECEIVER_ID = 2L;
    private static final long RESPONSE_ID = 1L;
    private static final String REQ_DESCRIPTION = "Java";

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;

    @Mock
    private MentorshipValidator validator;

    @Mock
    private List<MentorshipRequestFilter> mentorshipRequestFilters;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @Test
    public void requestMentorshipWhenUsersIdsEquals() {
        MentorshipRequestDto requestDto = new MentorshipRequestDto(REQ_DESCRIPTION, REQUESTER_ID, REQUESTER_ID);

        doThrow(new DataValidationException(MentorshipValidator.SAME_USER_ERR)).when(validator).validateEqualsId(REQUESTER_ID, REQUESTER_ID);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> mentorshipRequestService.requestMentorship(requestDto));
        assertThat(exception.getMessage()).isEqualTo(MentorshipValidator.SAME_USER_ERR);
    }

    @Test
    public void requestMentorshipWhenRequesterNotExists() {
        MentorshipRequestDto requestDto = new MentorshipRequestDto(REQ_DESCRIPTION, REQUESTER_ID, RECEIVER_ID);
        String errMessage = "Requester with ID: " + REQUESTER_ID + " doesn't exist";

        doThrow(new DataValidationException(errMessage)).when(validator).validateForEmptyRequester(REQUESTER_ID);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> mentorshipRequestService.requestMentorship(requestDto));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    public void requestMentorshipWhenReceiverNotExists() {
        MentorshipRequestDto requestDto = new MentorshipRequestDto(REQ_DESCRIPTION, REQUESTER_ID, RECEIVER_ID);
        String errMessage = "Receiver with ID: " + RECEIVER_ID + " doesn't exist";

        doThrow(new DataValidationException(errMessage)).when(validator).validateForEmptyReceiver(RECEIVER_ID);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> mentorshipRequestService.requestMentorship(requestDto));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    public void requestMentorshipWhenTooMuchRequestsInThreeMonth() {
        MentorshipRequestDto requestDto = new MentorshipRequestDto(REQ_DESCRIPTION, REQUESTER_ID, RECEIVER_ID);
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setCreatedAt(LocalDateTime.now().minusMonths(1));
        String errMessage = "A request for mentorship can only be made once every " + validator.getMIN_REQ_PERIOD() + " months";

        when(mentorshipRequestRepository.findLatestRequest(REQUESTER_ID, RECEIVER_ID)).thenReturn(Optional.of(mentorshipRequest));
        doThrow(new DataValidationException(errMessage)).when(validator).validateLastRequest(mentorshipRequest);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> mentorshipRequestService.requestMentorship(requestDto));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    public void requestMentorshipWithSuccess() {
        MentorshipRequestDto requestDto = new MentorshipRequestDto(REQ_DESCRIPTION, REQUESTER_ID, RECEIVER_ID);
        MentorshipResponseDto responseDto = new MentorshipResponseDto(RESPONSE_ID, RequestStatus.PENDING.name());
        MentorshipRequest mentorshipRequest = mock(MentorshipRequest.class);

        when(mentorshipRequestMapper.mentorshipRequestToResponseDto(mentorshipRequest)).thenReturn(responseDto);
        when(mentorshipRequestRepository.create(REQUESTER_ID, RECEIVER_ID, REQ_DESCRIPTION)).thenReturn(mentorshipRequest);

        MentorshipResponseDto actualResult = mentorshipRequestService.requestMentorship(requestDto);

        verify(mentorshipRequestRepository).create(REQUESTER_ID, RECEIVER_ID, REQ_DESCRIPTION);
        assertEquals(responseDto, actualResult);
    }

    @Test
    public void getRequestsWithFilters() {
        MentorshipRequestFilterDto filterDto = new MentorshipRequestFilterDto();
        filterDto.setDescription("Java");

        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setDescription(REQ_DESCRIPTION);

        MentorshipRequestDto requestDto = new MentorshipRequestDto(REQ_DESCRIPTION, REQUESTER_ID, RECEIVER_ID);

        List<MentorshipRequestDto> requestDtoList = List.of(requestDto);

        when(mentorshipRequestRepository.findAll()).thenReturn(List.of(mentorshipRequest));
        when(mentorshipRequestFilters.stream()).thenReturn(Stream.of(new MentorshipRequestDescriptionFilter()));
        when(mentorshipRequestMapper.toRequestDtoList(List.of(mentorshipRequest))).thenReturn(requestDtoList);

        var actualResult = mentorshipRequestService.getRequests(filterDto);
        assertIterableEquals(requestDtoList, actualResult);
    }

    @Test
    public void testAcceptRequestWhenRequestNotExists() {
        when(mentorshipRequestRepository.findById(RESPONSE_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> mentorshipRequestService.acceptRequest(RESPONSE_ID));
        assertThat(exception.getMessage()).isEqualTo("Mentorship request with ID " + RESPONSE_ID + " not found!");
    }

    @Test
    public void testAcceptRequestWhenMentorExistInRequesterList() {
        User receiver = User.builder().username("IVAN").build();
        User requester = User.builder().username("JOHN").mentors(List.of(receiver)).build();
        MentorshipRequest request = new MentorshipRequest();
        request.setRequester(requester);
        request.setReceiver(receiver);

        when(mentorshipRequestRepository.findById(RESPONSE_ID)).thenReturn(Optional.of(request));
        doThrow(new DataValidationException("Mentor " + receiver.getUsername() + " already assign to " + requester.getUsername()))
                .when(validator).validateExistMentorInRequesterList(requester, receiver);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> mentorshipRequestService.acceptRequest(RESPONSE_ID));
        assertThat(exception.getMessage()).isEqualTo("Mentor " + receiver.getUsername() + " already assign to " + requester.getUsername());
    }

    @Test
    public void testAcceptRequestWhenAcceptAndAddMentorToRequesterList() {
        User receiver = User.builder().username("IVAN").build();
        User requester = User.builder().username("JOHN").mentors(new ArrayList<>()).build();
        MentorshipRequest request = new MentorshipRequest();
        request.setId(RESPONSE_ID);
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);
        MentorshipResponseDto responseDto = new MentorshipResponseDto(RESPONSE_ID, RequestStatus.ACCEPTED.name());

        when(mentorshipRequestRepository.findById(RESPONSE_ID)).thenReturn(Optional.of(request));
        when(mentorshipRequestMapper.mentorshipRequestToResponseDto(request)).thenReturn(responseDto);

        MentorshipResponseDto actualResult = mentorshipRequestService.acceptRequest(RESPONSE_ID);

        assertEquals(responseDto, actualResult);
        assertThat(request.getStatus()).isEqualTo(RequestStatus.ACCEPTED);
    }

    @Test
    public void testRejectRequestWhenRequestNotExists() {
        RejectionDto rejection = new RejectionDto();
        rejection.setReason("Not enough time");

        when(mentorshipRequestRepository.findById(RESPONSE_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> mentorshipRequestService.rejectRequest(RESPONSE_ID, rejection));
        assertThat(exception.getMessage()).isEqualTo("Mentorship request with ID " + RESPONSE_ID + " not found!");
    }

    @Test
    public void testRejectRequestAndReturnDto() {
        RejectionDto rejection = new RejectionDto();
        rejection.setReason("Not enough time");
        MentorshipRequest request = new MentorshipRequest();
        request.setId(RESPONSE_ID);
        MentorshipResponseDto responseDto = new MentorshipResponseDto(RESPONSE_ID, RequestStatus.REJECTED.name());

        when(mentorshipRequestRepository.findById(RESPONSE_ID)).thenReturn(Optional.of(request));
        when(mentorshipRequestMapper.mentorshipRequestToResponseDto(request)).thenReturn(responseDto);

        MentorshipResponseDto actualResult = mentorshipRequestService.rejectRequest(RESPONSE_ID, rejection);
        assertEquals(responseDto, actualResult);
    }


}