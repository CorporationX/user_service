package school.faang.user_service.service.mentorship;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.mentorship.MentorshipRequestDescriptionFilter;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.filter.mentorship.MentorshipRequestReceiverFilter;
import school.faang.user_service.filter.mentorship.MentorshipRequestRequesterFilter;
import school.faang.user_service.filter.mentorship.MentorshipRequestStatusFilter;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validation.mentorship.MentorshipRequestValidator;

import static org.mockito.Mockito.mock;
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
    @Mock
    private List<MentorshipRequestFilter> filters;
    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    private MentorshipRequestDto requestDto;
    private MentorshipRequest request;
    private MentorshipRequestFilterDto requestFilter;

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
                .description("description")
                .requesterId(1L)
                .receiverId(2L)
                .status(RequestStatus.PENDING)
                .build();

        requestFilter = MentorshipRequestFilterDto.builder().build();

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

    @Test
    void getRequests_shouldInvokeRepositoryMethodFindAll() {
        mentorshipRequestService.getRequests(requestFilter);
        verify(mentorshipRequestRepository).findAll();
    }

    @Test
    void getRequests_shouldInvokeFilerMethodApply() {
        mentorshipRequestService.getRequests(requestFilter);

        MentorshipRequest mentorshipRequest = mock(MentorshipRequest.class);
        when(mentorshipRequestRepository.findAll()).thenReturn(List.of(mentorshipRequest));

        when(filters.stream()).thenReturn(Stream.of(
                new MentorshipRequestDescriptionFilter(),
                new MentorshipRequestRequesterFilter(),
                new MentorshipRequestReceiverFilter(),
                new MentorshipRequestStatusFilter()
        ));

        filters.forEach(filter -> verify(filter).apply(List.of(mentorshipRequest), requestFilter));
    }

    @Test
    void getRequests_shouldInvokeMapperMethodToDto() {
        MentorshipRequest mentorshipRequest = mock(MentorshipRequest.class);
        when(mentorshipRequestRepository.findAll()).thenReturn(List.of(mentorshipRequest));

        mentorshipRequestService.getRequests(requestFilter);

        verify(mentorshipRequestMapper).toDto(mentorshipRequest);
    }
}