package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.mapper.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.RequestStatus.ACCEPTED;
import static school.faang.user_service.entity.RequestStatus.PENDING;
import static school.faang.user_service.entity.RequestStatus.REJECTED;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {
    @Spy
    MentorshipRequestMapper mapper = new MentorshipRequestMapperImpl();
    @Mock
    MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    MentorshipRequestService mentorshipRequestService;

    private final long REQUESTER_ID = 1L;
    private final long RECEIVER_ID = 2L;
    private final LocalDateTime THREE_MONTH_AGO = LocalDateTime.now().minusMonths(3);

    private MentorshipRequest createMentorshipRequest(Long requesterId, long receiverId, String description, RequestStatus status, String requesterName, String receiverName) {
        User requester = new User();
        requester.setId(requesterId);
        requester.setUsername(requesterName);
        User receiver = new User();
        receiver.setId(receiverId);
        receiver.setUsername(receiverName);
        MentorshipRequest request = new MentorshipRequest();
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setDescription(description);
        request.setStatus(status);

        return request;
    }

    @Test
    void testCorrectRequest() {
        MentorshipRequest request1 = createMentorshipRequest(REQUESTER_ID, RECEIVER_ID, "1", ACCEPTED, "John", "Jim");
        MentorshipRequestDto requestDto1 = mapper.toDto(request1);
        when(userRepository.existsById(REQUESTER_ID)).thenReturn(true);
        when(userRepository.existsById(RECEIVER_ID)).thenReturn(true);
        assertDoesNotThrow(() -> mentorshipRequestService.requestMentorship(requestDto1));
    }

    @Test
    void testRequestWithoutDescription() {
        MentorshipRequest request1 = createMentorshipRequest(REQUESTER_ID, RECEIVER_ID, "1", ACCEPTED, "John", "Jim");
        MentorshipRequestDto requestDto1 = mapper.toDto(request1);
        requestDto1.setDescription("");
        assertThrows(Exception.class, () -> mentorshipRequestService.requestMentorship(requestDto1));
    }

    @Test
    void testRequesterDoesNotExists() {
        MentorshipRequest request1 = createMentorshipRequest(REQUESTER_ID, RECEIVER_ID, "1", ACCEPTED, "John", "Jim");
        MentorshipRequestDto requestDto1 = mapper.toDto(request1);
        when(userRepository.existsById(REQUESTER_ID)).thenReturn(false);
        assertThrows(IndexOutOfBoundsException.class, () -> mentorshipRequestService.requestMentorship(requestDto1));
    }

    @Test
    void testReceiverDoesNotExists() {
        MentorshipRequest request1 = createMentorshipRequest(REQUESTER_ID, RECEIVER_ID, "1", ACCEPTED, "John", "Jim");
        MentorshipRequestDto requestDto1 = mapper.toDto(request1);
        when(userRepository.existsById(REQUESTER_ID)).thenReturn(true);
        when(userRepository.existsById(RECEIVER_ID)).thenReturn(false);
        assertThrows(IndexOutOfBoundsException.class, () -> mentorshipRequestService.requestMentorship(requestDto1));
    }

    @Test
    void testReceiverIsRequester() {
        MentorshipRequest request1 = createMentorshipRequest(REQUESTER_ID, RECEIVER_ID, "1", ACCEPTED, "John", "Jim");
        MentorshipRequestDto requestDto1 = mapper.toDto(request1);
        requestDto1.setReceiverId(REQUESTER_ID);

        when(userRepository.existsById(REQUESTER_ID)).thenReturn(true);
        assertThrows(IndexOutOfBoundsException.class, () -> mentorshipRequestService.requestMentorship(requestDto1));
    }

    @Test
    void testOneRequestAt3Months() {
        MentorshipRequest request1 = createMentorshipRequest(REQUESTER_ID, RECEIVER_ID, "1", ACCEPTED, "John", "Jim");
        MentorshipRequestDto requestDto1 = mapper.toDto(request1);
        requestDto1.setUpdatedAt(THREE_MONTH_AGO.plusDays(1));

        when(userRepository.existsById(REQUESTER_ID)).thenReturn(true);
        when(userRepository.existsById(RECEIVER_ID)).thenReturn(true);
        when(mentorshipRequestRepository.findLatestRequest(REQUESTER_ID, RECEIVER_ID))
                .thenReturn(Optional.of(request1));
        assertThrows(RuntimeException.class, () -> mentorshipRequestService.requestMentorship(requestDto1));
    }

    @Test
    void testAcceptRequest() {
        MentorshipRequest request = createMentorshipRequest(REQUESTER_ID, RECEIVER_ID, "1", PENDING, "John", "Jim");
        when(mentorshipRequestRepository.findById(REQUESTER_ID)).thenReturn(Optional.of(request));
        mentorshipRequestService.acceptRequest(REQUESTER_ID);

        List<User> actualMentors = request.getRequester().getMentors();
        List<User> expectedMentors = List.of(request.getReceiver());

        assertEquals(actualMentors, expectedMentors);
        assertEquals(request.getStatus(), ACCEPTED);
    }

    @Test
    void requestExist() {
        when(mentorshipRequestRepository.findById(REQUESTER_ID)).thenReturn(null);
        assertThrows(NullPointerException.class, () -> mentorshipRequestService.acceptRequest(REQUESTER_ID));
    }

    @Test
    void receiverNotMentor() {
        MentorshipRequest request = createMentorshipRequest(REQUESTER_ID, RECEIVER_ID, "1", PENDING, "John", "Jim");
        User requester = request.getRequester();
        User receiver = request.getReceiver();
        requester.setMentors(List.of(receiver));
        request.setRequester(requester);

        when(mentorshipRequestRepository.findById(REQUESTER_ID)).thenReturn(Optional.of(request));
        assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.acceptRequest(REQUESTER_ID));
    }

    @Test
    void testRejectRequest() {
        when(mentorshipRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(request));
        mentorshipRequestService.rejectRequest(REQUEST_ID, rejection);
        assertEquals(request.getStatus(), REJECTED);
    }

    @Test
    void testRequestMentorshipInvokeValidateRejectRequest() {
        when(mentorshipRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(request));
        mentorshipRequestService.rejectRequest(REQUEST_ID, rejection);
        verify(validator)
                .validateRejectRequest(REQUEST_ID, rejection);
    }
}