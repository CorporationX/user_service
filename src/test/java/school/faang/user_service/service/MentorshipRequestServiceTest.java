package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.mapper.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validation.MentorshipRequestValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.RequestStatus.ACCEPTED;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {
    @Spy
    MentorshipRequestMapper mapper = new MentorshipRequestMapperImpl();
    @Mock
    MentorshipRequestValidator validator;
    @Mock
    MentorshipRequestRepository mentorshipRequestRepository;
    @InjectMocks
    MentorshipRequestService mentorshipRequestService;

    private final long REQUESTER_ID = 1L;
    private final long RECEIVER_ID = 2L;
    private final long REQUEST_ID = 0L;
    private final String DESCRIPTION = "description";
    private MentorshipRequest request;
    private MentorshipRequestDto requestDto;
    private User requester;
    private User receiver;

    @BeforeEach
    void setUp() {
        requester = new User();
        receiver = new User();
        requester.setId(REQUESTER_ID);
        receiver.setId(RECEIVER_ID);

        request = new MentorshipRequest();
        request.setId(0);
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setDescription(DESCRIPTION);

        requestDto  = MentorshipRequestDto.builder()
                .requesterId(REQUESTER_ID)
                .receiverId(RECEIVER_ID)
                .description(DESCRIPTION)
                .build();
    }

    @Test
    void testToDto() {
        MentorshipRequestDto fromRequest = mapper.toDto(request);
        assertEquals(requestDto, fromRequest);
    }

    @Test
    void testToEntity() {
        MentorshipRequest fromDto = mapper.toEntity(requestDto);
        assertEquals(request, fromDto);
    }

    @Test
    void testRequestMentorshipInvokeCreate() {
        mentorshipRequestService.requestMentorship(requestDto);
        verify(mentorshipRequestRepository)
            .create(REQUESTER_ID, RECEIVER_ID, DESCRIPTION);
    }

    @Test
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