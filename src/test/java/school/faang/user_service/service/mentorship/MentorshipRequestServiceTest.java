package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.MentorshipAcceptedEventDto;
import school.faang.user_service.dto.mentorship.request.MentorshipRequestCreationDto;
import school.faang.user_service.dto.mentorship.request.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.request.MentorshipRequestRejectionDto;
import school.faang.user_service.dto.mentorship.request.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.publisher.MentorshipAcceptedEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.filter.ReceiverFilter;
import school.faang.user_service.service.mentorship.filter.RequestFilter;
import school.faang.user_service.service.mentorship.filter.RequesterFilter;
import school.faang.user_service.service.mentorship.filter.StatusFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserContext userContext;

    @Spy
    private MentorshipRequestMapper requestMapper = Mappers.getMapper(MentorshipRequestMapper.class);

    @Mock
    private MentorshipAcceptedEventPublisher publisher;

    private List<RequestFilter> filters;
    private MentorshipRequestServiceImpl mentorshipRequestService;

    private MentorshipRequestCreationDto requestCreationDto;
    private MentorshipRequestRejectionDto rejectionDto;

    @BeforeEach
    void setUp() {
        RequestFilter filter1 = Mockito.spy(RequesterFilter.class);
        RequestFilter filter2 = Mockito.spy(ReceiverFilter.class);
        RequestFilter filter3 = Mockito.spy(StatusFilter.class);
        filters = List.of(filter1, filter2, filter3);
        mentorshipRequestService = new MentorshipRequestServiceImpl(
                mentorshipRequestRepository,
                userRepository,
                userContext,
                requestMapper,
                publisher,
                filters);
        mentorshipRequestService.setMinTimeRequestConstraint(3);
        requestCreationDto = MentorshipRequestCreationDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .description("test")
                .build();
        rejectionDto = new MentorshipRequestRejectionDto("test");
    }

    @Test
    @DisplayName("Create request")
    void mentorshipRequestServiceTest_createRequest() {
        when(userContext.getUserId()).thenReturn(1L);
        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(mentorshipRequestRepository.findLatestRequest(1L, 2L)).thenReturn(Optional.empty());
        when(mentorshipRequestRepository.existAcceptedRequest(1L, 2L)).thenReturn(false);
        MentorshipRequestDto expected = initMentorshipRequestDto(0L, 1L, 2L, "test", RequestStatus.PENDING);

        MentorshipRequestDto result = mentorshipRequestService.requestMentorship(requestCreationDto);

        assertEquals(expected, result);
        verify(userContext).getUserId();
        verify(userRepository, times(2)).existsById(any(Long.class));
        verify(mentorshipRequestRepository).findLatestRequest(1L, 2L);
        verify(mentorshipRequestRepository).existAcceptedRequest(1L, 2L);
        verify(mentorshipRequestRepository).save(any(MentorshipRequest.class));
    }

    @Test
    @DisplayName("Create request with request pass min time constraint")
    void mentorshipRequestServiceTest_createRequestPassMinTimeConstraint() {
        LocalDateTime requestDate = LocalDateTime.now().minusYears(1);
        MentorshipRequest oldRequest = initRequest(1L, 2L, RequestStatus.PENDING, requestDate);
        when(userContext.getUserId()).thenReturn(1L);
        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(mentorshipRequestRepository.findLatestRequest(1L, 2L)).thenReturn(Optional.of(oldRequest));
        when(mentorshipRequestRepository.existAcceptedRequest(1L, 2L)).thenReturn(false);
        MentorshipRequestDto expected = initMentorshipRequestDto(0L, 1L, 2L, "test", RequestStatus.PENDING);

        MentorshipRequestDto result = mentorshipRequestService.requestMentorship(requestCreationDto);

        assertEquals(expected, result);
        verify(userContext).getUserId();
        verify(userRepository, times(2)).existsById(any(Long.class));
        verify(mentorshipRequestRepository).findLatestRequest(1L, 2L);
        verify(mentorshipRequestRepository).existAcceptedRequest(1L, 2L);
        verify(mentorshipRequestRepository).save(any(MentorshipRequest.class));
    }

    @Test
    @DisplayName("Create request not from requester id")
    void mentorshipRequestServiceTest_createRequestNotFromRequesterId() {
        when(userContext.getUserId()).thenReturn(3L);

        assertThrows(DataValidationException.class,
                () -> mentorshipRequestService.requestMentorship(requestCreationDto));
        verify(userContext).getUserId();
    }

    @Test
    @DisplayName("Create request with non existing requester id")
    void mentorshipRequestServiceTest_createRequestNonExistingRequesterId() {
        when(userContext.getUserId()).thenReturn(1L);
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> mentorshipRequestService.requestMentorship(requestCreationDto));
        verify(userContext).getUserId();
        verify(userRepository).existsById(any(Long.class));
    }

    @Test
    @DisplayName("Create request with non existing receiver id")
    void mentorshipRequestServiceTest_createRequestNonExistingReceiverId() {
        when(userContext.getUserId()).thenReturn(1L);
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> mentorshipRequestService.requestMentorship(requestCreationDto));
        verify(userContext).getUserId();
        verify(userRepository, times(2)).existsById(any(Long.class));
    }

    @Test
    @DisplayName("Create request with same receiver and requester id")
    void mentorshipRequestServiceTest_createRequestWithSameReceiverAndRequesterId() {
        MentorshipRequestCreationDto request = MentorshipRequestCreationDto.builder()
                .requesterId(1L)
                .receiverId(1L)
                .description("test")
                .build();
        when(userContext.getUserId()).thenReturn(1L);

        assertThrows(DataValidationException.class,
                () -> mentorshipRequestService.requestMentorship(request));
        verify(userContext).getUserId();
    }

    @Test
    @DisplayName("Create request with already accepted request")
    void mentorshipRequestServiceTest_createRequestWithAlreadyAcceptedRequest() {
        when(userContext.getUserId()).thenReturn(1L);
        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(mentorshipRequestRepository.existAcceptedRequest(1L, 2L)).thenReturn(true);

        assertThrows(DataValidationException.class,
                () -> mentorshipRequestService.requestMentorship(requestCreationDto));
        verify(userContext).getUserId();
        verify(userRepository, times(2)).existsById(any(Long.class));
        verify(mentorshipRequestRepository).existAcceptedRequest(1L, 2L);
    }

    @Test
    @DisplayName("Create request with request sent under time constraint")
    void mentorshipRequestServiceTest_createRequestWithRequestSentUnderTimeConstraint() {
        LocalDateTime requestDate = LocalDateTime.now().minusDays(1);
        MentorshipRequest oldRequest = initRequest(1L, 2L, RequestStatus.PENDING, requestDate);
        when(userContext.getUserId()).thenReturn(1L);
        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(mentorshipRequestRepository.findLatestRequest(1L, 2L)).thenReturn(Optional.of(oldRequest));
        when(mentorshipRequestRepository.existAcceptedRequest(1L, 2L)).thenReturn(false);


        assertThrows(DataValidationException.class,
                () -> mentorshipRequestService.requestMentorship(requestCreationDto));
        verify(userContext).getUserId();
        verify(userRepository, times(2)).existsById(any(Long.class));
        verify(mentorshipRequestRepository).findLatestRequest(1L, 2L);
        verify(mentorshipRequestRepository).existAcceptedRequest(1L, 2L);
    }

    @Test
    @DisplayName("Get mentorship requests with filters")
    void mentorshipRequestServiceTest_getMentorshipRequestWithFilters() {
        RequestFilterDto filterDto = initFilterDto(1L, 2L, null);
        List<MentorshipRequest> requests = List.of(
                initRequest(2L, 1L, RequestStatus.PENDING, null),
                initRequest(3L, 1L, RequestStatus.ACCEPTED, null),
                initRequest(3L, 2L, RequestStatus.REJECTED, null));
        when(mentorshipRequestRepository.findAll()).thenReturn(requests);
        List<MentorshipRequestDto> expected = List.of(
                initMentorshipRequestDto(0L, 2L, 1L, null, RequestStatus.PENDING));

        List<MentorshipRequestDto> result = mentorshipRequestService.getRequests(filterDto);
        assertEquals(expected, result);
        verify(mentorshipRequestRepository).findAll();
    }

    @Test
    @DisplayName("Get filtered empty list of requests")
    void mentorshipRequestServiceTest_getFilteredEmptyList() {
        RequestFilterDto filterDto = initFilterDto(1L, 2L, null);
        List<MentorshipRequest> requests = List.of();
        when(mentorshipRequestRepository.findAll()).thenReturn(requests);

        List<MentorshipRequestDto> result = mentorshipRequestService.getRequests(filterDto);
        assertTrue(result.isEmpty());
        verify(mentorshipRequestRepository).findAll();
    }

    @Test
    @DisplayName("Accept request")
    void mentorshipRequestServiceTest_acceptRequest() {
        MentorshipRequest request = initRequest(1L, 2L, RequestStatus.PENDING, null);
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(userContext.getUserId()).thenReturn(2L);

        mentorshipRequestService.acceptRequest(1L);

        assertEquals(RequestStatus.ACCEPTED, request.getStatus());
        verify(mentorshipRequestRepository).findById(1L);
        verify(userContext).getUserId();
        verify(publisher).publish(any(MentorshipAcceptedEventDto.class));
        verify(mentorshipRequestRepository).save(any(MentorshipRequest.class));
    }

    @Test
    @DisplayName("Accept request with wrong status")
    void mentorshipRequestServiceTest_acceptRequestWithWrongStatus() {
        MentorshipRequest request = initRequest(1L, 2L, RequestStatus.ACCEPTED, null);
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        assertThrows(DataValidationException.class,
                () -> mentorshipRequestService.acceptRequest(1L));
        verify(mentorshipRequestRepository).findById(1L);
    }

    @Test
    @DisplayName("Accept non-existent request")
    void mentorshipRequestServiceTest_acceptNonExistentRequest() {
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> mentorshipRequestService.acceptRequest(1L));
        verify(mentorshipRequestRepository).findById(1L);
    }

    @Test
    @DisplayName("Accept request not from receiver id")
    void mentorshipRequestServiceTest_acceptRequestNotFromReceiverId() {
        MentorshipRequest request = initRequest(1L, 2L, RequestStatus.PENDING, null);
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(userContext.getUserId()).thenReturn(3L);

        assertThrows(DataValidationException.class,
                () -> mentorshipRequestService.acceptRequest(1L));
        verify(mentorshipRequestRepository).findById(1L);
        verify(userContext).getUserId();
    }

    @Test
    @DisplayName("Reject request")
    void mentorshipRequestServiceTest_rejectRequest() {
        MentorshipRequest request = initRequest(1L, 2L, RequestStatus.PENDING, null);
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(userContext.getUserId()).thenReturn(2L);

        mentorshipRequestService.rejectRequest(1L, rejectionDto);

        assertEquals(RequestStatus.REJECTED, request.getStatus());
        assertEquals(rejectionDto.reason(), request.getRejectionReason());
        verify(mentorshipRequestRepository).findById(1L);
        verify(userContext).getUserId();
    }

    @Test
    @DisplayName("Reject request with wrong status")
    void mentorshipRequestServiceTest_rejectRequestWithWrongStatus() {
        MentorshipRequest request = initRequest(1L, 2L, RequestStatus.ACCEPTED, null);
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        assertThrows(DataValidationException.class,
                () -> mentorshipRequestService.rejectRequest(1L, rejectionDto));
        verify(mentorshipRequestRepository).findById(1L);
    }

    @Test
    @DisplayName("Reject request not from requester id")
    void mentorshipRequestServiceTest_rejectRequestNotFromRequesterId() {
        MentorshipRequest request = initRequest(1L, 2L, RequestStatus.PENDING, null);
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(userContext.getUserId()).thenReturn(3L);

        assertThrows(DataValidationException.class,
                () -> mentorshipRequestService.rejectRequest(1L, rejectionDto));
        verify(mentorshipRequestRepository).findById(1L);
        verify(userContext).getUserId();
    }

    @Test
    @DisplayName("Reject non-existent request")
    void mentorshipRequestServiceTest_rejectNonExistentRequest() {
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> mentorshipRequestService.rejectRequest(1L, rejectionDto));
        verify(mentorshipRequestRepository).findById(1L);
    }

    private MentorshipRequestDto initMentorshipRequestDto(
            Long id, Long requesterId, Long receiverId, String description, RequestStatus status) {
        return MentorshipRequestDto.builder()
                .id(id)
                .requesterId(requesterId)
                .receiverId(receiverId)
                .description(description)
                .status(status)
                .build();
    }

    private MentorshipRequest initRequest(
            Long requesterId, Long receiverId, RequestStatus status, LocalDateTime createdAt) {
        MentorshipRequest request = new MentorshipRequest();
        User requester = new User();
        requester.setId(requesterId);
        User receiver = new User();
        receiver.setId(receiverId);
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setStatus(status);
        request.setCreatedAt(createdAt);
        return request;
    }

    private RequestFilterDto initFilterDto(Long receiverId, Long requesterId, RequestStatus status) {
        return RequestFilterDto.builder()
                .requesterIdFilter(requesterId)
                .receiverIdFilter(receiverId)
                .statusFilter(status)
                .build();
    }
}
