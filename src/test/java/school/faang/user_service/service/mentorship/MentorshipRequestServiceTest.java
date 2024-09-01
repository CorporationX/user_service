package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.MentorshipAcceptedEvent;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.event.MentorshipRequestEvent;
import school.faang.user_service.filter.mentorship.MentorshipRequestDescriptionFilter;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.filter.mentorship.MentorshipRequestReceiverFilter;
import school.faang.user_service.filter.mentorship.MentorshipRequestRequesterFilter;
import school.faang.user_service.filter.mentorship.MentorshipRequestStatusFilter;
import school.faang.user_service.mapper.mentorship.MentorshipRequestEventMapper;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapperImpl;
import school.faang.user_service.publisher.MentorshipRequestEventPublisher;
import school.faang.user_service.redisPublisher.MentorshipAcceptedEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {
    @Captor
    private ArgumentCaptor<RequestStatus> requestStatusCaptor;
    @Captor
    private ArgumentCaptor<MentorshipRequest> mentorshipRequestCaptor;

    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;
    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private List<MentorshipRequestFilter> mentorshipRequestFilterList;
    @Mock
    private MentorshipRequestEventPublisher mentorshipRequestEventPublisher;
    @Mock
    private MentorshipRequestEventMapper mentorshipRequestEventMapper;
    @Mock
    private MentorshipAcceptedEventPublisher mentorshipAcceptedEventPublisher;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;
    private MentorshipRequestMapperImpl mentorshipRequestMapperImpl;

    @BeforeEach
    public void setUp() {
        mentorshipRequestMapperImpl = new MentorshipRequestMapperImpl();
    }

    @Test
    @DisplayName("testing requestMentorship methods execution")
    public void testRequestMentorshipValidatorExecution() {
        MentorshipRequestDto mentorshipRequestDto = MentorshipRequestDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .createdAt(LocalDateTime.now())
                .description("description").build();
        MentorshipRequestEvent mentorshipRequestEvent = new MentorshipRequestEvent();

        when(mentorshipRequestEventMapper.toEvent(mentorshipRequestCaptor.capture())).thenReturn(mentorshipRequestEvent);
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        verify(mentorshipRequestValidator, times(1))
                .validateParticipantsAndRequestFrequency(
                        mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getReceiverId(),
                        mentorshipRequestDto.getCreatedAt());
        verify(mentorshipRequestRepository, times(1))
                .create(mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getReceiverId(),
                        mentorshipRequestDto.getDescription()
                );
        verify(mentorshipRequestEventMapper, times(1)).toEvent(mentorshipRequestCaptor.getValue());
        verify(mentorshipRequestEventPublisher, times(1)).publish(mentorshipRequestEvent);
        verify(mentorshipRequestMapper, times(1)).toDto(mentorshipRequestCaptor.getValue());
    }

    @Test
    @DisplayName("testing getRequests repository findAll() method execution")
    public void testGetRequestsRepositorySelectionExecution() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto();

        when(mentorshipRequestFilterList.stream()).thenReturn(getAllFilters());
        mentorshipRequestService.getRequests(mentorshipRequestFilterDto);
        verify(mentorshipRequestRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("testing getRequests with appropriate request selection")
    public void testGetRequestsWithAppropriateRequestSelection() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = MentorshipRequestFilterDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .description("description")
                .status(RequestStatus.PENDING).build();
        List<MentorshipRequest> mentorshipRequestList = getMentorshipRequestList();

        when(mentorshipRequestRepository.findAll()).thenReturn(mentorshipRequestList);
        when(mentorshipRequestFilterList.stream()).thenReturn(getAllFilters());
        when(mentorshipRequestMapper.toDtoList(List.of(mentorshipRequestList.get(0))))
                .thenReturn(mentorshipRequestMapperImpl.toDtoList(List.of(mentorshipRequestList.get(0))));

        List<MentorshipRequestDto> resultRequests =
                mentorshipRequestService.getRequests(mentorshipRequestFilterDto);

        assertEquals(1, resultRequests.size());
        assertEquals(mentorshipRequestMapperImpl.toDto(mentorshipRequestList.get(0)), resultRequests.get(0));
    }

    @Test
    @DisplayName("testing acceptRequest with non existing mentorship request")
    public void testAcceptRequestWithNonExistingMentorshipRequest() {
        long requestId = 1L;

        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestService.acceptRequest(requestId));
        assertEquals("Could not find Mentorship Request in database by id: " + requestId,
                exception.getMessage());
    }

    @Test
    @DisplayName("test acceptRequest validator validateRequestStatusIsPending() method execution")
    public void testAcceptRequestValidatorExecution() {
        long requestId = 1L;
        User requester = User.builder().mentors(new ArrayList<>()).id(2L).build();
        User receiver = User.builder().mentees(new ArrayList<>()).id(1L).build();
        MentorshipRequest mentorshipRequest = MentorshipRequest.builder()
                .id(requestId)
                .requester(requester)
                .receiver(receiver)
                .status(RequestStatus.PENDING).build();

        doNothing().when(mentorshipAcceptedEventPublisher).publish(any(MentorshipAcceptedEvent.class));
        when(mentorshipRequestRepository.save(any())).thenReturn(mentorshipRequest);
        when(mentorshipRequestRepository.findById(mentorshipRequest.getId())).thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.acceptRequest(mentorshipRequest.getId());
        verify(mentorshipRequestValidator, times(1))
                .validateRequestStatusIsPending(requestStatusCaptor.capture());
    }

    @Test
    @DisplayName("test acceptRequest validator validateReceiverIsNotMentorOfRequester() method execution")
    public void testAcceptRequestValidatorReceiverIsNotMentorExecution() {
        long requestId = 1L;
        User requester = User.builder().mentors(new ArrayList<>()).id(2L).build();
        User receiver = User.builder().mentees(new ArrayList<>()).id(1L).build();
        MentorshipRequest mentorshipRequest = MentorshipRequest.builder()
                .id(requestId)
                .requester(requester)
                .receiver(receiver)
                .status(RequestStatus.PENDING).build();

        doNothing().when(mentorshipAcceptedEventPublisher).publish(any(MentorshipAcceptedEvent.class));
        when(mentorshipRequestRepository.save(any())).thenReturn(mentorshipRequest);
        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(mentorshipRequest));
        mentorshipRequestService.acceptRequest(requestId);
        verify(mentorshipRequestValidator, times(1))
                .validateReceiverIsNotMentorOfRequester(requester, receiver);
    }

    @Test
    @DisplayName("testing acceptRequest repository save() method execution")
    public void testAcceptRequestRepositorySaveExecution() {
        long requestId = 1L;
        User requester = User.builder().mentors(new ArrayList<>()).id(2L).build();
        User receiver = User.builder().mentees(new ArrayList<>()).id(1L).build();
        MentorshipRequest mentorshipRequest = MentorshipRequest.builder()
                .id(requestId)
                .requester(requester)
                .receiver(receiver)
                .status(RequestStatus.PENDING).build();

        doNothing().when(mentorshipAcceptedEventPublisher).publish(any(MentorshipAcceptedEvent.class));
        when(mentorshipRequestRepository.save(any())).thenReturn(mentorshipRequest);
        when(mentorshipRequestRepository.findById(mentorshipRequest.getId()))
                .thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.acceptRequest(mentorshipRequest.getId());
        verify(mentorshipRequestRepository, times(1)).save(mentorshipRequest);
    }

    @Test
    @DisplayName("test rejectRequest with non existing mentorship request")
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
    @DisplayName("testing rejectionRequest validator validateRequestStatusIsPending() method execution")
    public void testRejectionRequestValidatorExecution() {
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
    @DisplayName("testing rejectionRequest repository save() method execution")
    public void testRejectionRequestRepositorySaveExecution() {
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

    private Stream<MentorshipRequestFilter> getAllFilters() {
        return Stream.of(
                new MentorshipRequestRequesterFilter(),
                new MentorshipRequestReceiverFilter(),
                new MentorshipRequestDescriptionFilter(),
                new MentorshipRequestStatusFilter()
        );
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
}