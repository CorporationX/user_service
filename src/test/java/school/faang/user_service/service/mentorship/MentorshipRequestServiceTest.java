package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.dto.MentorshipRequestedEventDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectionReasonDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.mentorship.MentorshipRequestDescriptionFilter;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.filter.mentorship.MentorshipRequestReceiverFilter;
import school.faang.user_service.filter.mentorship.MentorshipRequestRequesterFilter;
import school.faang.user_service.filter.mentorship.MentorshipRequestStatusFilter;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.publisher.MentorshipRequestedEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
    @Mock
    private  MentorshipRequestedEventPublisher mentorshipRequestedEventPublisher;
    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;
    private MentorshipRequestDto requestDto;
    private MentorshipRequest request;
    private MentorshipRequestFilterDto requestFilter;
    private RejectionReasonDto rejectionReason;

    @BeforeEach
    void setUp() {
        request = MentorshipRequest.builder()
                .id(1L)
                .description("description")
                .requester(User.builder().id(1L).mentors(new ArrayList<>()).build())
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
        rejectionReason = RejectionReasonDto.builder().reason("reason").build();

        when(mentorshipRequestMapper.toEntity(requestDto)).thenReturn(request);
        when(mentorshipRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));
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
        when(mentorshipRequestRepository.findAll()).thenReturn(List.of(request));

        mentorshipRequestService.getRequests(requestFilter);
        verify(mentorshipRequestMapper).toDto(request);
    }

    @Test
    void acceptRequest_shouldInvokeRepositoryMethodFindById() {
        mentorshipRequestService.acceptRequest(request.getId());
        verify(mentorshipRequestRepository).findById(request.getId());
    }

    @Test
    void acceptRequest_shouldAddMentorToRequesterMentorsList() {
        assertEquals(0, request.getRequester().getMentors().size());
        mentorshipRequestService.acceptRequest(request.getId());
        assertAll(() -> {
            assertEquals(1, request.getRequester().getMentors().size());
            assertEquals(request.getRequester().getMentors().get(0), request.getReceiver());
        });
    }

    @Test
    void acceptRequest_shouldChangeRequestStatusToAccepted() {
        assertEquals(RequestStatus.PENDING, request.getStatus());
        mentorshipRequestService.acceptRequest(request.getId());
        assertEquals(RequestStatus.ACCEPTED, request.getStatus());
    }

    @Test
    void acceptRequest_shouldInvokeMapperMethodToDto() {
        mentorshipRequestService.acceptRequest(request.getId());
        verify(mentorshipRequestMapper).toDto(request);
    }

    @Test
    void acceptRequest_shouldThrowEntityNotFoundException() {
        when(mentorshipRequestRepository.findById(request.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> mentorshipRequestService.acceptRequest(request.getId()),
                "Request with id" + request.getId() + "not found.");
    }

    @Test
    void acceptRequest_shouldThrowMentorshipRequestValidationException() {
        request.getRequester().getMentors().add(request.getReceiver());

        assertThrows(DataValidationException.class,
                () -> mentorshipRequestService.acceptRequest(request.getId()),
                "Receiver is already mentor of this requester.");
    }

    @Test
    void rejectRequest_shouldInvokeRepositoryMethodFindById() {
        mentorshipRequestService.rejectRequest(request.getId(), rejectionReason);
        verify(mentorshipRequestRepository).findById(request.getId());
    }

    @Test
    void rejectRequest_shouldChangeRequestStatusToRejected() {
        assertEquals(RequestStatus.PENDING, request.getStatus());
        mentorshipRequestService.rejectRequest(request.getId(), rejectionReason);
        assertEquals(RequestStatus.REJECTED, request.getStatus());
    }

    @Test
    void rejectRequest_shouldSetRejectionReason() {
        mentorshipRequestService.rejectRequest(request.getId(), rejectionReason);
        assertEquals(rejectionReason.getReason(), request.getRejectionReason());
    }

    @Test
    void rejectRequest_shouldInvokeMapperMethodToDto() {
        mentorshipRequestService.rejectRequest(request.getId(), rejectionReason);
        verify(mentorshipRequestMapper).toDto(request);
    }

    @Test
    void rejectRequest_shouldThrowEntityNotFoundException() {
        when(mentorshipRequestRepository.findById(request.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> mentorshipRequestService.rejectRequest(request.getId(), rejectionReason),
                "Request with id" + request.getId() + "not found.");
    }
    @Test
    void rejectRequest_shouldInvokePublishAndSentEvent() {
        mentorshipRequestService.requestMentorship(requestDto);
        verify(mentorshipRequestedEventPublisher).publish(any(MentorshipRequestedEventDto.class));
    }
}