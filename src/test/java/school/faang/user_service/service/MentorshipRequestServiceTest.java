package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.mapper.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.filter.*;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.RequestStatus.ACCEPTED;
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
    @Mock
    MentorshipRequestRepository mentorshipRequestRepository;
    @InjectMocks
    MentorshipRequestService mentorshipRequestService;

    private final long REQUESTER_ID = 1L;
    private final long RECEIVER_ID = 2L;
    private final LocalDateTime THREE_MONTH_AGO = LocalDateTime.now().minusMonths(3);
    private RequestFilterDto filter = new RequestFilterDto();

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
    void testDescriptionFilterFilters() {
        MentorshipRequest request1 = createMentorshipRequest(REQUESTER_ID, RECEIVER_ID, "1", ACCEPTED, "John", "Jim");
        MentorshipRequest request2 = createMentorshipRequest(REQUESTER_ID, RECEIVER_ID, "2", REJECTED, "Jared", "Java");
        MentorshipRequest request3 = createMentorshipRequest(REQUESTER_ID, RECEIVER_ID, "3", PENDING, "Jamal", "Jabahaba");

        List<MentorshipRequest> requests = List.of(request1, request2, request3);
        filter.setDescriptionPattern("1");
        when(mentorshipRequestRepository.findAll()).thenReturn(requests);
        when(mentorshipRequestFilters.stream()).thenReturn(Stream.of(new MentorshipRequestDescriptionFilter()));

        assertEquals(mentorshipRequestService.getRequests(filter), List.of(mapper.toDto(request1)));
    }

    @Test
    void testRequesterNameFilterTest() {
        MentorshipRequest request1 = createMentorshipRequest(REQUESTER_ID, RECEIVER_ID, "1", ACCEPTED, "John", "Jim");
        MentorshipRequest request2 = createMentorshipRequest(REQUESTER_ID, RECEIVER_ID, "2", REJECTED, "Jared", "Java");
        MentorshipRequest request3 = createMentorshipRequest(REQUESTER_ID, RECEIVER_ID, "3", PENDING, "Jamal", "Jabahaba");

        List<MentorshipRequest> requests = List.of(request1, request2, request3);
        filter.setRequesterNamePattern("Jamal");
        when(mentorshipRequestRepository.findAll()).thenReturn(requests);
        when(mentorshipRequestFilters.stream()).thenReturn(Stream.of(new MentorshipRequestRequesterNameFilter()));

        assertEquals(mentorshipRequestService.getRequests(filter), List.of(mapper.toDto(request3)));
    }

    @Test
    void testReceiverNameFilterTest() {
        MentorshipRequest request1 = createMentorshipRequest(REQUESTER_ID, RECEIVER_ID, "1", ACCEPTED, "John", "Jim");
        MentorshipRequest request2 = createMentorshipRequest(REQUESTER_ID, RECEIVER_ID, "2", REJECTED, "Jared", "Java");
        MentorshipRequest request3 = createMentorshipRequest(REQUESTER_ID, RECEIVER_ID, "3", PENDING, "Jamal", "Jabahaba");

        List<MentorshipRequest> requests = List.of(request1, request2, request3);
        filter.setReceiverNamePattern("Java");
        when(mentorshipRequestRepository.findAll()).thenReturn(requests);
        when(mentorshipRequestFilters.stream()).thenReturn(Stream.of(new MentorshipRequestReceiverNameFilter()));

        assertEquals(mentorshipRequestService.getRequests(filter), List.of(mapper.toDto(request2)));
    }

    @Test
    void testRequestStatusFilterTest() {
        MentorshipRequest request1 = createMentorshipRequest(REQUESTER_ID, RECEIVER_ID, "1", ACCEPTED, "John", "Jim");
        MentorshipRequest request2 = createMentorshipRequest(REQUESTER_ID, RECEIVER_ID, "2", REJECTED, "Jared", "Java");
        MentorshipRequest request3 = createMentorshipRequest(REQUESTER_ID, RECEIVER_ID, "3", PENDING, "Jamal", "Jabahaba");

        List<MentorshipRequest> requests = List.of(request1, request2, request3);
        filter.setRequestStatusPattern(PENDING);
        when(mentorshipRequestRepository.findAll()).thenReturn(requests);
        when(mentorshipRequestFilters.stream()).thenReturn(Stream.of(new MentorshipRequestStatusFilter()));

        assertEquals(mentorshipRequestService.getRequests(filter), List.of(mapper.toDto(request3)));
    void testRequestMentorshipInvokeValidateRequest() {
        mentorshipRequestService.requestMentorship(requestDto);
        verify(validator)
            .validateRequest(request);
    }

    @Test
    void testAcceptRequest() {
        when(mentorshipRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(request));
        mentorshipRequestService.acceptRequest(REQUEST_ID);

        List<User> actualMentors = requester.getMentors();
        List<User> expectedMentors = List.of(receiver);

        assertEquals(actualMentors, expectedMentors);
        assertEquals(request.getStatus(), ACCEPTED);
    }
    @Test
    void testRequestMentorshipInvokeValidateAcceptRequest() {
        when(mentorshipRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(request));
        mentorshipRequestService.acceptRequest(REQUEST_ID);
        verify(validator)
                .validateAcceptRequest(REQUEST_ID);
    }
}