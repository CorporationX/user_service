package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRejectionDto;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipMapper;
import school.faang.user_service.mapper.MentorshipMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Spy
    private MentorshipMapper mentorshipMapper;
    @Spy
    private MentorshipMapperImpl mentorshipMapperImpl;
    @Mock
    private UserRepository userRepository;
//    @Mock
//    private List<MentorshipRequestFilter> mentorshipRequestFilters;
//    @Mock
//    private MentorshipRequest mentorshipRequest;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    private static final Long REQUESTER_ID = 1L;
    private static final Long REQUESTER_ID_FAIL = 23L;
    private static final Long RECEIVER_ID = 2L;
    private static final Long RECEIVER_ID_FAIL = 24L;
    private static final String DESCRIPTION = "I want you to be my mentor";
    private static final RequestStatus REQUEST_STATUS = RequestStatus.PENDING;
    private static final Long REQUEST_ID = 1L;
    private static final Long REQUEST_ID_FAIL = 1000L;
    private static final Long REJECT_ID = 1L;
    private static final Long REJECT_ID_FAIL = 1000L;
    private static final String REJECT_REASON = "I'm on vacation.";
    private static final Integer NUMBER_MONTH = 3;
    private static final Integer NUMBER_INVOCATION = 1;

    @Test
    public void testRequesterIdIsNotExist() {
        MentorshipRequestDto mentorshipRequestDto = prepareDataToDto(
                REQUESTER_ID_FAIL,
                RECEIVER_ID,
                DESCRIPTION,
                REQUEST_STATUS,
                REQUEST_ID,
                LocalDateTime.now());
        when(userRepository.existsById(mentorshipRequestDto.getRequesterId())).thenReturn(false);

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(mentorshipRequestDto));
    }

    @Test
    public void testReceiverIdIsNotExist() {
        MentorshipRequestDto mentorshipRequestDto = prepareDataToDto(
                REQUESTER_ID,
                RECEIVER_ID_FAIL,
                DESCRIPTION,
                REQUEST_STATUS,
                REQUEST_ID,
                LocalDateTime.now());
        when(userRepository.existsById(mentorshipRequestDto.getRequesterId())).thenReturn(true);
        when(userRepository.existsById(mentorshipRequestDto.getReceiverId())).thenReturn(false);

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(mentorshipRequestDto));
    }

    @Test
    public void testCheckIfDifferentUsers() {
        MentorshipRequestDto mentorshipRequestDto = prepareDataToDto(
                REQUESTER_ID,
                REQUESTER_ID,
                DESCRIPTION,
                REQUEST_STATUS,
                REQUEST_ID,
                LocalDateTime.now());
        when(userRepository.existsById(mentorshipRequestDto.getRequesterId())).thenReturn(true);
        when(userRepository.existsById(mentorshipRequestDto.getReceiverId())).thenReturn(true);

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(mentorshipRequestDto));
    }

    @Test
    public void testCheckLastRequest() {
        MentorshipRequestDto mentorshipRequestDto = prepareDataForLastRequest(LocalDateTime.now().minusDays(4));
        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(mentorshipRequestDto));
    }

    @Test
    public void testCreateMentorshipRequest() {
        MentorshipRequestDto mentorshipRequestDto = prepareDataForLastRequest(LocalDateTime.now().minusMonths(6));

        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        verify(mentorshipRequestRepository, Mockito.times(NUMBER_INVOCATION))
                .create(mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getReceiverId(),
                        mentorshipRequestDto.getDescription());
    }

    @Test
    public void testAcceptRequestNotExist() {
        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.acceptRequest(REQUEST_ID_FAIL));
    }

    @Test
    public void testAcceptRequestMentorExist() {
        MentorshipRequest mentorshipRequest = prepareDataForRequest(REQUEST_ID, true);

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.acceptRequest(REQUEST_ID));
    }

    @Test
    public void testAcceptRequest() {
        MentorshipRequest mentorshipRequest = prepareDataForRequest(REQUEST_ID, false);
        MentorshipRequestDto mentorshipRequestDto = mentorshipRequestService.acceptRequest(REQUEST_ID);
        verify(mentorshipRequestRepository, Mockito.times(NUMBER_INVOCATION)).save(mentorshipRequest);
    }

    @Test
    public void testRejectRequestNotExist() {
        MentorshipRejectionDto mentorshipRejectionDto = prepareDataToRejectionDto(REJECT_ID_FAIL, REJECT_REASON);
        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.rejectRequest(mentorshipRejectionDto));
    }

    @Test
    public void testRejectRequest() {
        MentorshipRequest mentorshipRequest = prepareDataForRequest(REJECT_ID, false);
        MentorshipRejectionDto mentorshipRejectionDto = prepareDataToRejectionDto(REJECT_ID, REJECT_REASON);

        MentorshipRequestDto mentorshipRequestDto = mentorshipRequestService.rejectRequest(mentorshipRejectionDto);
        verify(mentorshipRequestRepository, Mockito.times(NUMBER_INVOCATION)).save(mentorshipRequest);
    }

    @Test
    public void testGetRequests() {

    }

    private MentorshipRejectionDto prepareDataToRejectionDto(Long rejectId, String rejectReason) {
        MentorshipRejectionDto mentorshipRejectionDto = new MentorshipRejectionDto();
        mentorshipRejectionDto.setId(rejectId);
        mentorshipRejectionDto.setReason(rejectReason);

        return mentorshipRejectionDto;
    }


    private MentorshipRequest prepareDataForRequest(Long requestId, boolean isExist) {
        User userRequester = preparaDataForUser(REQUESTER_ID, new ArrayList<>());
        User userReceiver = preparaDataForUser(RECEIVER_ID, new ArrayList<>());
        if (isExist) {
            addMentorForUser(userRequester, userReceiver);
        }

        MentorshipRequest mentorshipRequest = prepareRequestForAccept(requestId,
                userRequester,
                userReceiver,
                REQUEST_STATUS,
                LocalDateTime.now());
        when(mentorshipRequestRepository
                .findById(requestId)).thenReturn(Optional.of(mentorshipRequest));

        return mentorshipRequest;
    }

    private MentorshipRequest prepareRequestForAccept(Long requestId, User userRequester, User userReceiver,
                                                      RequestStatus status, LocalDateTime date) {
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setId(requestId);
        mentorshipRequest.setRequester(userRequester);
        mentorshipRequest.setReceiver(userReceiver);
        mentorshipRequest.setStatus(status);
        mentorshipRequest.setCreatedAt(date);
        return mentorshipRequest;
    }

    private void addMentorForUser(User userRequester, User userReceiver) {
        userRequester.getMentors().add(userReceiver);
    }

    private User preparaDataForUser(Long id, List<User> mentors) {
        User user = new User();
        user.setId(id);
        user.setMentors(mentors);
        return user;
    }

    private MentorshipRequestDto prepareDataToDto(Long requesterId,
                                                  Long receiverId,
                                                  String description,
                                                  RequestStatus requestStatus,
                                                  Long requestId,
                                                  LocalDateTime data) {
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setRequesterId(requesterId);
        mentorshipRequestDto.setReceiverId(receiverId);
        mentorshipRequestDto.setDescription(description);
        mentorshipRequestDto.setStatus(requestStatus);
        mentorshipRequestDto.setId(requestId);
        mentorshipRequestDto.setCreatedAt(data);
        return mentorshipRequestDto;
    }

    private MentorshipRequestDto prepareDataForLastRequest(LocalDateTime date) {
        MentorshipRequest request = new MentorshipRequest();
        request.setCreatedAt(date);
        mentorshipRequestService.setNumberMonthsMembership(NUMBER_MONTH);
        MentorshipRequestDto mentorshipRequestDto = prepareDataToDto(
                REQUESTER_ID,
                RECEIVER_ID,
                DESCRIPTION,
                REQUEST_STATUS,
                REQUEST_ID,
                LocalDateTime.now());
        when(userRepository.existsById(mentorshipRequestDto.getRequesterId())).thenReturn(true);
        when(userRepository.existsById(mentorshipRequestDto.getReceiverId())).thenReturn(true);
        when(mentorshipRequestRepository.findLatestRequest(mentorshipRequestDto.getRequesterId(),
                mentorshipRequestDto.getReceiverId()))
                .thenReturn(Optional.of(request));
        return mentorshipRequestDto;
    }
}
