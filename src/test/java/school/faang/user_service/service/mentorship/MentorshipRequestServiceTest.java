package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.mentorship.MentorshipRequestDescriptionFilter;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.filter.mentorship.MentorshipRequestReceiverFilter;
import school.faang.user_service.filter.mentorship.MentorshipRequestRequesterFilter;
import school.faang.user_service.filter.mentorship.MentorshipRequestStatusFilter;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {
    @Captor
    private ArgumentCaptor<RequestStatus> requestStatusCaptor;
    @Captor
    private ArgumentCaptor<MentorshipRequest> mentorshipRequestCaptor;

    @Mock
    private MentorshipRequestMapperImpl mentorshipRequestMapper;
    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private List<MentorshipRequestFilter> mentorshipRequestFilterList;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @Test
    public void testRequestMentorshipValidatorExecution() {
        MentorshipRequestDto mentorshipRequestDto = MentorshipRequestDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .createdAt(LocalDateTime.now())
                .description("description").build();

        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        verify(mentorshipRequestValidator, times(1))
                .validateMentorshipRequestReceiverAndRequesterExistence(
                        mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getReceiverId());
        verify(mentorshipRequestValidator, times(1))
                .validateReflection(
                        mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getReceiverId());
        verify(mentorshipRequestValidator, times(1))
                .validateMentorshipRequestFrequency(
                        mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getReceiverId(),
                        mentorshipRequestDto.getCreatedAt());
    }

    @Test
    public void testRequestMentorshipRepositoryCreateExecution() {
        MentorshipRequestDto mentorshipRequestDto = MentorshipRequestDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .description("description").build();

        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        verify(mentorshipRequestRepository, times(1))
                .create(mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getReceiverId(),
                        mentorshipRequestDto.getDescription()
                );
    }

    @Test
    public void testRequestMentorshipMapperToDtoExecution() {
        MentorshipRequestDto mentorshipRequestDto = MentorshipRequestDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .description("description").build();

        MentorshipRequest mentorshipRequestAfterCreation = mentorshipRequestMapper.toEntity(mentorshipRequestDto);
        when(mentorshipRequestRepository.create(
                mentorshipRequestDto.getRequesterId(),
                mentorshipRequestDto.getReceiverId(),
                mentorshipRequestDto.getDescription()))
                .thenReturn(mentorshipRequestAfterCreation);

        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        verify(mentorshipRequestMapper, times(1))
                .toDto(mentorshipRequestCaptor.capture());
    }

    @Test
    public void testGetRequestsRepositorySelectionExecution() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto();

        when(mentorshipRequestFilterList.stream()).thenReturn(getAllFilters());
        mentorshipRequestService.getRequests(mentorshipRequestFilterDto);
        verify(mentorshipRequestRepository, times(1)).findAll();
    }

    @Test
    public void testGetRequestsWithAppropriateRequestSelection() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = MentorshipRequestFilterDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .description("description")
                .status(RequestStatus.PENDING).build();
        List<MentorshipRequest> mentorshipRequestList = getMentorshipRequestList();

        when(mentorshipRequestRepository.findAll()).thenReturn(mentorshipRequestList);
        when(mentorshipRequestFilterList.stream()).thenReturn(getAllFilters());
        List<MentorshipRequestDto> resultRequests = mentorshipRequestService.getRequests(mentorshipRequestFilterDto);

        assertEquals(1, resultRequests.size());
        assertEquals(mentorshipRequestMapper.toDto(mentorshipRequestList.get(0)), resultRequests.get(0));
    }

    private List<MentorshipRequest> getMentorshipRequestList() {
        return List.of(
                getMentorshipRequest(1L, 2L, "description", RequestStatus.PENDING),
                getMentorshipRequest(1L, 3L, "testing", RequestStatus.REJECTED),
                getMentorshipRequest(2L, 3L, "some", RequestStatus.ACCEPTED),
                getMentorshipRequest(3L, 4L, "some", RequestStatus.PENDING)
        );
    }

    private MentorshipRequest getMentorshipRequest(long requesterId, long receiverId,
                                                   String description, RequestStatus requestStatus) {
        return MentorshipRequest.builder()
                .requester(User.builder().id(requesterId).build())
                .receiver(User.builder().id(receiverId).build())
                .description(description)
                .status(requestStatus)
                .build();
    }

    @Test
    public void testAcceptRequestWithNonExistingMentorshipRequest() {
        long requestId = 1L;

        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestService.acceptRequest(requestId));
        assertEquals("Could not find Mentorship Request in database by id: " + requestId,
                exception.getMessage());
    }

    @Test
    public void testAcceptRequestWithValidatorExecutionVerification() {
        MentorshipRequest mentorshipRequest = MentorshipRequest.builder()
                .id(1L)
                .status(RequestStatus.PENDING).build();

        when(mentorshipRequestRepository.findById(mentorshipRequest.getId()))
                .thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.acceptRequest(mentorshipRequest.getId());
        verify(mentorshipRequestValidator, times(1))
                .validateRequestStatusIsPending(requestStatusCaptor.capture());
    }

    @Test
    public void testAcceptRequestWithRepositorySaveExecutionVerification() {
        MentorshipRequest mentorshipRequest = MentorshipRequest.builder()
                .id(1L)
                .status(RequestStatus.PENDING).build();

        when(mentorshipRequestRepository.findById(mentorshipRequest.getId()))
                .thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.acceptRequest(mentorshipRequest.getId());
        verify(mentorshipRequestRepository, times(1)).save(mentorshipRequest);
    }

    @Test
    public void testAcceptRequestWithToDtoExecution() {
        MentorshipRequest mentorshipRequest = MentorshipRequest.builder()
                .id(1L)
                .status(RequestStatus.PENDING).build();

        when(mentorshipRequestRepository.findById(mentorshipRequest.getId()))
                .thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.acceptRequest(mentorshipRequest.getId());
        verify(mentorshipRequestMapper, times(1)).toDto(mentorshipRequestCaptor.capture());
    }

    @Test
    public void testRejectRequestWithNonExistingMentorshipRequest() {
        long requestId = 1L;
        RejectionDto rejectionDto = RejectionDto.builder()
                .rejectionReason("rejection reason").build();

        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestService.rejectRequest(requestId, rejectionDto));
        assertEquals("Could not find Mentorship Request in database by id: " + requestId,
                exception.getMessage());
    }

    @Test
    public void testRejectionRequestWithValidationRequestExecution() {
        MentorshipRequest mentorshipRequest = MentorshipRequest.builder()
                .id(1L)
                .status(RequestStatus.PENDING).build();
        RejectionDto rejectionDto = RejectionDto.builder()
                .rejectionReason("rejection reason").build();

        when(mentorshipRequestRepository.findById(mentorshipRequest.getId()))
                .thenReturn(Optional.of(mentorshipRequest));
        mentorshipRequestService.rejectRequest(mentorshipRequest.getId(), rejectionDto);
        verify(mentorshipRequestValidator, times(1))
                .validateRequestStatusIsPending(requestStatusCaptor.capture());
    }

    @Test
    public void testRejectionRequestWithRepositorySaveExecution() {
        MentorshipRequest mentorshipRequest = MentorshipRequest.builder()
                .id(1L)
                .status(RequestStatus.PENDING).build();
        RejectionDto rejectionDto = RejectionDto.builder()
                .rejectionReason("rejection reason").build();

        when(mentorshipRequestRepository.findById(mentorshipRequest.getId()))
                .thenReturn(Optional.of(mentorshipRequest));
        mentorshipRequestService.rejectRequest(mentorshipRequest.getId(), rejectionDto);
        verify(mentorshipRequestRepository, times(1))
                .save(mentorshipRequestCaptor.capture());
    }

    @Test
    public void testRejectionRequestWithToDtoExecution() {
        MentorshipRequest mentorshipRequest = MentorshipRequest.builder()
                .id(1L)
                .status(RequestStatus.PENDING).build();
        RejectionDto rejectionDto = RejectionDto.builder()
                .rejectionReason("rejection reason").build();

        when(mentorshipRequestRepository.findById(mentorshipRequest.getId()))
                .thenReturn(Optional.of(mentorshipRequest));
        mentorshipRequestService.rejectRequest(mentorshipRequest.getId(), rejectionDto);
        verify(mentorshipRequestMapper, times(1))
                .toDto(mentorshipRequestCaptor.capture());
    }

    private Stream<MentorshipRequestFilter> getAllFilters() {
        return Stream.of(
                new MentorshipRequestRequesterFilter(),
                new MentorshipRequestReceiverFilter(),
                new MentorshipRequestDescriptionFilter(),
                new MentorshipRequestStatusFilter()
        );
    }
}