package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.mapper.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.filter.MentorshipRequestFilter;
import school.faang.user_service.validation.MentorshipRequestValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {
    @Spy
    MentorshipRequestMapper mapper = new MentorshipRequestMapperImpl();
    @Mock
    MentorshipRequestValidator validator;
    @Mock
    MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    MentorshipRequestFilter mentorshipRequestFilter;
    @InjectMocks
    MentorshipRequestService mentorshipRequestService;

    private final long REQUESTER_ID = 1L;
    private final long RECEIVER_ID = 2L;
    private final String DESCRIPTION = "description";
    private MentorshipRequest request;
    private MentorshipRequestDto requestDto;

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

        requestDto  = MentorshipRequestDto.builder()
                .requesterId(REQUESTER_ID)
                .receiverId(RECEIVER_ID)
                .description(DESCRIPTION)
                .build();

        List<MentorshipRequestFilter> filters = List.of(mentorshipRequestFilter);
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
    void testRequestMentorshipInvokeValidate() {
        mentorshipRequestService.requestMentorship(requestDto);
        verify(validator)
            .validate(request);
    }
}