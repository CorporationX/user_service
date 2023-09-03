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
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.filter.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.RequestStatus.ACCEPTED;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {
    @Mock
    MentorshipRequestMapper mapper;
    @Mock
    MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    List<MentorshipRequestFilter> mentorshipRequestFilters;
    @InjectMocks
    MentorshipRequestService mentorshipRequestService;

    private final long REQUESTER_ID = 1L;
    private final long RECEIVER_ID = 2L;
    private final String DESCRIPTION = "description";
    private final LocalDateTime THREE_MONTH_AGO = LocalDateTime.now().minusMonths(3);
    private MentorshipRequest request;
    private MentorshipRequestDto requestDto;
    private RequestFilterDto filter = new RequestFilterDto();

    private MentorshipRequest createMentorshipRequest(Long requesterId, long receiverId, String description, RequestStatus status) {
        User requester = new User();
        requester.setId(requesterId);
        User receiver = new User();
        receiver.setId(receiverId);
        MentorshipRequest request = new MentorshipRequest();
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setDescription(description);
        request.setStatus(status);

        return request;
    }

    @BeforeEach
    void setUp() {
        User requester = new User();
        requester.setId(REQUESTER_ID);
        User receiver = new User();
        receiver.setId(RECEIVER_ID);

        request = new MentorshipRequest();
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setDescription(DESCRIPTION);

        requestDto = MentorshipRequestDto.builder()
                .requesterId(REQUESTER_ID)
                .receiverId(RECEIVER_ID)
                .description(DESCRIPTION)
                .build();

        when(mapper.toEntity(requestDto)).thenReturn(request);
    }

    @Test
    void testRequestMentorshipInvokeCreate() {
        when(userRepository.existsById(REQUESTER_ID)).thenReturn(true);
        when(userRepository.existsById(RECEIVER_ID)).thenReturn(true);

        mentorshipRequestService.requestMentorship(requestDto);
        verify(mentorshipRequestRepository)
                .create(REQUESTER_ID, RECEIVER_ID, DESCRIPTION);
    }

    @Test
    void testCorrectRequest() {
        when(userRepository.existsById(REQUESTER_ID)).thenReturn(true);
        when(userRepository.existsById(RECEIVER_ID)).thenReturn(true);
        assertDoesNotThrow(() -> mentorshipRequestService.requestMentorship(requestDto));
    }

    @Test
    void testRequestWithoutDescription() {
        requestDto.setDescription("");
        assertThrows(Exception.class, () -> mentorshipRequestService.requestMentorship(requestDto));
    }

    @Test
    void testRequesterDoesNotExists() {
        when(userRepository.existsById(REQUESTER_ID)).thenReturn(false);
        assertThrows(IndexOutOfBoundsException.class, () -> mentorshipRequestService.requestMentorship(requestDto));
    }

    @Test
    void testReceiverDoesNotExists() {
        when(userRepository.existsById(REQUESTER_ID)).thenReturn(true);
        when(userRepository.existsById(RECEIVER_ID)).thenReturn(false);
        assertThrows(IndexOutOfBoundsException.class, () -> mentorshipRequestService.requestMentorship(requestDto));
    }

    @Test
    void testReceiverIsRequester() {
        requestDto.setReceiverId(REQUESTER_ID);

        when(userRepository.existsById(REQUESTER_ID)).thenReturn(true);
        assertThrows(IndexOutOfBoundsException.class, () -> mentorshipRequestService.requestMentorship(requestDto));
    }

    @Test
    void testOneRequestAt3Months() {
        requestDto.setUpdatedAt(THREE_MONTH_AGO.plusDays(1));

        when(userRepository.existsById(REQUESTER_ID)).thenReturn(true);
        when(userRepository.existsById(RECEIVER_ID)).thenReturn(true);
        when(mentorshipRequestRepository.findLatestRequest(REQUESTER_ID, RECEIVER_ID))
                .thenReturn(Optional.of(request));
        assertThrows(RuntimeException.class, () -> mentorshipRequestService.requestMentorship(requestDto));
    }

    @Test
    void testDescriptionFilterFilters() {
        MentorshipRequest request1 = createMentorshipRequest(REQUESTER_ID, RECEIVER_ID, "1", ACCEPTED);
        MentorshipRequest request2 = createMentorshipRequest(REQUESTER_ID, RECEIVER_ID, "2", ACCEPTED);
        MentorshipRequest request3 = createMentorshipRequest(REQUESTER_ID, RECEIVER_ID, "3", ACCEPTED);
        List<MentorshipRequest> requests = List.of(request1, request2, request3);
        filter.setDescriptionPattern("1");
        when(mentorshipRequestRepository.findAll()).thenReturn(requests);
        when(mentorshipRequestFilters.stream()).thenReturn(Stream.of(new MentorshipRequestDescriptionFilter()));
        //when(mapper.toDto(???))

        assertEquals(mentorshipRequestService.getRequests(filter), List.of(request1));
    }
}