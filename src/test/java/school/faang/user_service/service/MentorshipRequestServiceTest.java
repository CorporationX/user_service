package school.faang.user_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.constants.ErrorMessages.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.mentor.MentorshipRequestDto;
import school.faang.user_service.dto.mentor.RejectionDto;
import school.faang.user_service.dto.mentor.RequestFilterDto;
import school.faang.user_service.dto.mentor.RequestStatusDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.mentor.DescriptionFilter;
import school.faang.user_service.filter.mentor.RequestFilter;
import school.faang.user_service.filter.mentor.StatusFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.mapper.RequestFilterMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {
    private static final int REQUEST_COOLDOWN_MONTHS = 3;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private UserService userService;
    @Mock
    private RequestFilter requestDescriptionFilter;
    @Mock
    private RequestFilter requestStatusFilter;

    @Spy
    private MentorshipRequestMapper mentorshipRequestMapper = Mappers.getMapper(MentorshipRequestMapper.class);
    @Spy
    private RequestFilterMapper requestFilterMapper = Mappers.getMapper(RequestFilterMapper.class);

    @Captor
    private ArgumentCaptor<MentorshipRequest> mentorshipRequestCaptor;
    @Captor
    private ArgumentCaptor<List<MentorshipRequest>> mentorshipRequestStreamCaptor;

    private User requester, receiver;
    private MentorshipRequestDto mentorshipRequestDto;
    private RequestFilterDto requestFilterDto;
    private RejectionDto rejectionDto;
    private long requestId;
    private MentorshipRequest mentorshipRequest;

    @BeforeEach
    void setUp() {
        requester = new User();
        receiver = new User();
        requester.setId(1L);
        receiver.setId(2L);
        receiver.setMentors(new ArrayList<>());

        mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setDescription("Valid description for mentorship request");
        mentorshipRequestDto.setRequesterId(requester.getId());
        mentorshipRequestDto.setReceiverId(receiver.getId());

        mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        mentorshipRequest.setId(requestId);

        rejectionDto = new RejectionDto();
        rejectionDto.setRejectionReason("Reason reject");

        requestId = 1L;

        requestFilterDto = new RequestFilterDto();
        requestFilterDto.setDescription("Description");
        requestFilterDto.setStatus(RequestStatusDto.ACCEPTED);

        requestDescriptionFilter = new StatusFilter();
        requestStatusFilter = new DescriptionFilter();
        ReflectionTestUtils.setField(mentorshipRequestService, "filters", List.of(requestDescriptionFilter, requestStatusFilter));
    }

    //Positive
    @Test
    void testRequestMentorship() {
        mockExistsById(true);
        when(userService.findById(requester.getId())).thenReturn(requester);
        when(userService.findById(receiver.getId())).thenReturn(receiver);

        mentorshipRequestService.requestMentorship(mentorshipRequestDto);

        verify(mentorshipRequestRepository, times(1)).save(mentorshipRequestCaptor.capture());
        MentorshipRequest mentorshipRequest = mentorshipRequestCaptor.getValue();
        assertEquals(RequestStatus.PENDING, mentorshipRequest.getStatus());
        assertEquals(receiver, mentorshipRequest.getReceiver());
        assertEquals(requester, mentorshipRequest.getRequester());
    }

    @Test
    void testGetRequests_withRejectedRequests_shouldReturnEmptyList() {
        MentorshipRequest request1 = new MentorshipRequest();
        MentorshipRequest request2 = new MentorshipRequest();
        request1.setId(1);
        request1.setDescription("Description");
        request1.setStatus(RequestStatus.REJECTED);
        request2.setId(2);
        request2.setDescription("abc");
        request2.setStatus(RequestStatus.ACCEPTED);
        when(mentorshipRequestRepository.findAll()).thenReturn(List.of(request1, request2));

        List<RequestFilterDto> result = mentorshipRequestService.getRequests(requestFilterDto);

        verify(requestFilterMapper, times(1)).toListDto(mentorshipRequestStreamCaptor.capture());
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetRequests_withAcceptedRequests_shouldReturnNonEmptyList() {
        MentorshipRequest request1 = new MentorshipRequest();
        MentorshipRequest request2 = new MentorshipRequest();
        request1.setId(1);
        request1.setDescription("Description");
        request1.setStatus(RequestStatus.ACCEPTED);
        request2.setId(2);
        request2.setDescription("abc");
        request2.setStatus(RequestStatus.REJECTED);
        when(mentorshipRequestRepository.findAll()).thenReturn(List.of(request1, request2));

        List<RequestFilterDto> result = mentorshipRequestService.getRequests(requestFilterDto);

        verify(requestFilterMapper, times(1)).toListDto(mentorshipRequestStreamCaptor.capture());
        assertEquals(1, result.size());
    }

    @Test
    void testAcceptRequest() {
        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(mentorshipRequest));
        when(mentorshipRequestRepository.save(mentorshipRequest)).thenReturn(mentorshipRequest);

        mentorshipRequestService.acceptRequest(requestId);

        assertEquals(RequestStatus.ACCEPTED, mentorshipRequest.getStatus());
        assertEquals(receiver, mentorshipRequest.getReceiver());
        verify(mentorshipRequestRepository, times(1)).save(mentorshipRequestCaptor.capture());
        assertTrue(receiver.getMentors().contains(requester));
    }

    @Test
    void testRejectRequest() {
        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(mentorshipRequest));
        when(mentorshipRequestRepository.save(mentorshipRequest)).thenReturn(mentorshipRequest);

        mentorshipRequestService.rejectRequest(requestId, rejectionDto);

        assertEquals(RequestStatus.REJECTED, mentorshipRequest.getStatus());
        assertEquals(rejectionDto.getRejectionReason(), mentorshipRequest.getRejectionReason());
        verify(mentorshipRequestRepository, times(1)).save(mentorshipRequestCaptor.capture());
    }

    //Negative
    @Test
    void testRequestMentorshipNullDto() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                mentorshipRequestService.requestMentorship(null));

        assertEquals(ERROR_NULL_MENTORSHIP_REQUEST_DTO, exception.getMessage());
    }

    @Test
    void testRequestMentorshipErrorShotDescription() {
        mentorshipRequestDto.setDescription(" ");

        assertThrows(IllegalArgumentException.class, () ->
                mentorshipRequestService.requestMentorship(mentorshipRequestDto));
    }

    @Test
    void testRequestMentorshipErrorSelfRequest() {
        mentorshipRequestDto.setRequesterId(requester.getId());
        mentorshipRequestDto.setReceiverId(requester.getId());
        when(mentorshipRequestRepository.existsById(requester.getId())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                mentorshipRequestService.requestMentorship(mentorshipRequestDto));

        assertEquals(ERROR_SELF_REQUEST, exception.getMessage());
    }

    @Test
    void testRequestMentorshipNotFoundUser() {
        List<Long> missingUser = Stream.of(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId())
                .filter(id -> !mentorshipRequestRepository.existsById(id))
                .toList();
        mockExistsById(false);

        String missingIds = missingUser.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                mentorshipRequestService.requestMentorship(mentorshipRequestDto));

        assertEquals(getUserNotFoundError(missingIds), exception.getMessage());
    }

    @Test
    void testRequestMentorshipAfterCooldown() {
        LocalDateTime oldRequestTime = LocalDateTime.now().minusMonths(1);
        MentorshipRequest oldRequest = new MentorshipRequest();
        oldRequest.setCreatedAt(oldRequestTime);

        mockExistsById(true);
        when(mentorshipRequestRepository.findLatestRequest(requester.getId(), receiver.getId()))
                .thenReturn(Optional.of(oldRequest));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                mentorshipRequestService.requestMentorship(mentorshipRequestDto));

        assertEquals(getFrequentRequestError(REQUEST_COOLDOWN_MONTHS), exception.getMessage());
    }

    @Test
    void testGetRequestsNullDto() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                mentorshipRequestService.getRequests(null));

        assertEquals(ERROR_NULL_REQUEST_DTO, exception.getMessage());
    }

    @Test
    void testAcceptRequestNoFindRequest() {
        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                mentorshipRequestService.acceptRequest(requestId));

        assertEquals(getAbsentRequestError(requestId), exception.getMessage());
    }

    @Test
    void testAcceptRequestSelfRequest() {
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(requester);
        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(mentorshipRequest));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.acceptRequest(requestId));

        assertEquals(ERROR_SELF_REQUEST, exception.getMessage());
    }

    @Test
    void testAcceptRequestIsAlreadyMentor() {
        List<User> mentors = new ArrayList<>();
        mentors.add(requester);
        receiver.setMentors(mentors);
        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(mentorshipRequest));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.acceptRequest(requestId));

        assertEquals(ERROR_ALREADY_MENTOR, exception.getMessage());
    }

    @Test
    void testRejectRequestNullDto() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                mentorshipRequestService.rejectRequest(0, null));

        assertEquals(ERROR_NULL_REJECTION_DTO, exception.getMessage());
    }

    @Test
    void testRejectRequestNullReason() {
        rejectionDto.setRejectionReason(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                mentorshipRequestService.rejectRequest(requestId, rejectionDto));

        assertEquals(ERROR_EMPTY_REJECTION, exception.getMessage());
    }

    @Test
    void testRejectRequestIsBlankReason() {
        rejectionDto.setRejectionReason("");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                mentorshipRequestService.rejectRequest(requestId, rejectionDto));

        assertEquals(ERROR_EMPTY_REJECTION, exception.getMessage());
    }

    @Test
    void testRejectRequestNoFoundRequest() {
        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                mentorshipRequestService.rejectRequest(requestId, rejectionDto));

        assertEquals(getAbsentRequestError(requestId), exception.getMessage());
    }

    private void mockExistsById(boolean existById) {
        when(mentorshipRequestRepository.existsById(requester.getId())).thenReturn(existById);
        when(mentorshipRequestRepository.existsById(receiver.getId())).thenReturn(existById);
    }
}