package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceImplTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MentorshipRepository mentorshipRepository;

    @InjectMocks
    private MentorshipRequestServiceImpl mentorshipRequestService;

    private User requester;
    private User receiver;
    private MentorshipRequest mentorshipRequest;
    private RequestFilterDto filter;

    @BeforeEach
    void setUp() {
        requester = new User();
        requester.setId(1L);
        receiver = new User();
        receiver.setId(2L);

        mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setDescription("Valid description");

        filter = new RequestFilterDto();
        filter.setDescription("test");
        filter.setRequesterId(1L);
        filter.setResponderId(2L);
        filter.setStatus(RequestStatus.PENDING);
    }

    @Test
    void requestMentorship_shouldThrowException_whenRequesterIsReceiver() {
        mentorshipRequest.setReceiver(requester);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.requestMentorship(mentorshipRequest));

        assertEquals("Cannot request from yourself", exception.getMessage());
    }

    @Test
    void requestMentorship_shouldThrowException_whenDescriptionIsTooShort() {
        mentorshipRequest.setDescription("abc");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.requestMentorship(mentorshipRequest));

        assertEquals("Mentorship description is too short, it should be at least 4 characters", exception.getMessage());
    }

    @Test
    void requestMentorship_shouldThrowException_whenUsersNotFound() {
        when(userRepository.findAllById(any())).thenReturn(List.of());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.requestMentorship(mentorshipRequest));

        assertEquals("One or both users not found", exception.getMessage());
    }

    @Test
    void requestMentorship_shouldThrowException_whenRecentRequestExists() {
        when(userRepository.findAllById(any())).thenReturn(List.of(requester, receiver));
        MentorshipRequest latestRequest = new MentorshipRequest();
        latestRequest.setCreatedAt(LocalDateTime.now()
                                           .minusMonths(1));
        when(mentorshipRequestRepository.findLatestRequestByRequester(requester.getId()))
                .thenReturn(Optional.of(latestRequest));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.requestMentorship(mentorshipRequest));

        assertEquals("A mentorship request has already been made within the last 3 months", exception.getMessage());
    }

    @Test
    void requestMentorship_shouldSaveRequest_whenValid() {
        when(userRepository.findAllById(any())).thenReturn(List.of(requester, receiver));
        when(mentorshipRequestRepository.findLatestRequestByRequester(requester.getId()))
                .thenReturn(Optional.empty());
        when(mentorshipRequestRepository.save(any(MentorshipRequest.class)))
                .thenReturn(mentorshipRequest);

        MentorshipRequest result = mentorshipRequestService.requestMentorship(mentorshipRequest);

        assertEquals(mentorshipRequest, result);
        verify(mentorshipRequestRepository, times(1)).save(mentorshipRequest);
    }

    @Test
    void acceptRequest_shouldThrowException_whenRequestNotFound() {
        when(mentorshipRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> mentorshipRequestService.acceptRequest(1L));

        assertEquals("Mentorship request with id 1 doesnt exist", exception.getMessage());
    }

    @Test
    void acceptRequest_shouldThrowException_whenRequestAlreadyAccepted() {
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);

        when(mentorshipRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(mentorshipRequest));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.acceptRequest(1L));

        assertEquals("Mentorship request with id 1 already accepted", exception.getMessage());
    }

    @Test
    void acceptRequest_shouldThrowException_whenMentorshipAlreadyExists() {
        mentorshipRequest.setStatus(RequestStatus.PENDING);

        when(mentorshipRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(mentorshipRequest));
        when(mentorshipRepository.findByMentorAndMentee(receiver.getId(), requester.getId()))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.acceptRequest(1L));

        assertEquals("Mentorship already exists", exception.getMessage());
    }

    @Test
    void acceptRequest_shouldSaveRequest_whenValid() {
        mentorshipRequest.setStatus(RequestStatus.PENDING);

        when(mentorshipRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(mentorshipRequest));
        when(mentorshipRepository.findByMentorAndMentee(receiver.getId(), requester.getId()))
                .thenReturn(false);
        when(mentorshipRequestRepository.save(any(MentorshipRequest.class)))
                .thenReturn(mentorshipRequest);

        MentorshipRequest result = mentorshipRequestService.acceptRequest(1L);

        assertEquals(RequestStatus.ACCEPTED, result.getStatus());
        verify(mentorshipRequestRepository, times(1)).save(mentorshipRequest);
    }

    @Test
    void rejectRequest_shouldThrowException_whenRequestNotFound() {
        when(mentorshipRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> mentorshipRequestService.rejectRequest(1L, new RejectionDto()));

        assertEquals("Mentorship request with id 1 doesnt exist", exception.getMessage());
    }

    @Test
    void rejectRequest_shouldThrowException_whenRequestAlreadyAccepted() {
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);

        when(mentorshipRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(mentorshipRequest));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.rejectRequest(1L, new RejectionDto()));

        assertEquals("Mentorship request with id 1 already accepted", exception.getMessage());
    }

    @Test
    void rejectRequest_shouldThrowException_whenRequestAlreadyRejected() {
        mentorshipRequest.setStatus(RequestStatus.REJECTED);

        when(mentorshipRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(mentorshipRequest));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.rejectRequest(1L, new RejectionDto()));

        assertEquals("Mentorship request with id 1 already rejected", exception.getMessage());
    }

    @Test
    void rejectRequest_shouldSaveRequest_whenValid() {
        mentorshipRequest.setStatus(RequestStatus.PENDING);

        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setReason("Not interested");

        when(mentorshipRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(mentorshipRequest));
        when(mentorshipRequestRepository.save(any(MentorshipRequest.class)))
                .thenReturn(mentorshipRequest);

        MentorshipRequest result = mentorshipRequestService.rejectRequest(1L, rejectionDto);

        assertEquals(RequestStatus.REJECTED, result.getStatus());
        assertEquals(rejectionDto.getReason(), result.getDescription());
        verify(mentorshipRequestRepository, times(1)).save(mentorshipRequest);
    }

    @Test
    void getRequests_shouldReturnFilteredRequests() {
        MentorshipRequest request1 = new MentorshipRequest();
        MentorshipRequest request2 = new MentorshipRequest();

        when(mentorshipRequestRepository.findAllByFilter(anyString(), anyLong(), anyLong(), anyInt()))
                .thenReturn(List.of(request1, request2));

        List<MentorshipRequest> result = mentorshipRequestService.getRequests(filter);

        assertEquals(2, result.size());
        verify(mentorshipRequestRepository, times(1)).findAllByFilter(anyString(), anyLong(), anyLong(), anyInt());
    }
}
