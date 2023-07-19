package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validation.mentorship.MentorshipRequestValidator;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MentorshipRequestServiceTest {
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;
    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;
    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;
    private MentorshipRequestDto requestDto;
    private MentorshipRequest request;

    @BeforeEach
    void setUp() {
        request = MentorshipRequest.builder()
                .id(1L)
                .description("description")
                .requester(User.builder().id(1L).build())
                .receiver(User.builder().id(2L).build())
                .status(RequestStatus.PENDING)
                .build();

        requestDto = MentorshipRequestDto.builder()
                .id(1L)
                .description("test")
                .requesterId(1L)
                .receiverId(2L)
                .status("PENDING")
                .build();

        when(mentorshipRequestMapper.toEntity(requestDto)).thenReturn(request);
    }

    @Test
    void requestMentorship_shouldInvokeValidatorMethodValidate() {
        mentorshipRequestService.requestMentorship(requestDto);
        verify(mentorshipRequestValidator).validate(requestDto);
    }

    @Test
    void requestMentorship_shouldInvokeMapperMethodToEntity() {
        mentorshipRequestService.requestMentorship(requestDto);
        verify(mentorshipRequestMapper).toEntity(requestDto);
    }

    @Test
    void requestMentorship_shouldInvokeRepositoryMethodSave() {
        mentorshipRequestService.requestMentorship(requestDto);
        verify(mentorshipRequestRepository).save(request);
    }
}