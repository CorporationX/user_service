package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
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
    private ArgumentCaptor<MentorshipRequest> mentorshipRequestCaptor;
    @Spy
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
        MentorshipRequestDto mentorshipRequestDto = getMentorshipRequestDto(
                1L, 2L,
                LocalDateTime.now(), "description"
        );

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
        MentorshipRequestDto mentorshipRequestDto = getMentorshipRequestDto(
                1L, 2L,
                LocalDateTime.now(), "description"
        );

        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        verify(mentorshipRequestRepository, times(1))
                .create(mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getReceiverId(),
                        mentorshipRequestDto.getDescription()
                );
    }

    @Test
    public void testRequestMentorshipMapperToDtoExecution() {
        MentorshipRequestDto mentorshipRequestDto = getMentorshipRequestDto(
                1L, 2L,
                LocalDateTime.now(), "description"
        );

        MentorshipRequest mentorshipRequestAfterCreation = mentorshipRequestMapper.toEntity(mentorshipRequestDto);
        when(mentorshipRequestRepository.create(
                mentorshipRequestDto.getRequesterId(),
                mentorshipRequestDto.getReceiverId(),
                mentorshipRequestDto.getDescription()))
                .thenReturn(mentorshipRequestAfterCreation);

        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        verify(mentorshipRequestMapper, times(1))
                .toDto(mentorshipRequestCaptor.capture());
        MentorshipRequest mentorshipRequest = mentorshipRequestCaptor.getValue();

        assertEquals(mentorshipRequestDto.getRequesterId(), mentorshipRequest.getRequester().getId());
        assertEquals(mentorshipRequestDto.getReceiverId(), mentorshipRequest.getReceiver().getId());
        assertEquals(mentorshipRequestDto.getDescription(), mentorshipRequest.getDescription());
        assertEquals(mentorshipRequestDto.getCreatedAt(), mentorshipRequest.getCreatedAt());
    }

    @Test
    public void testGetRequestsRepositorySelectionExecution() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = getMentorshipRequestFilterDto(
                1L, 2L,
                "description", RequestStatus.PENDING);

        when(mentorshipRequestFilterList.stream()).thenReturn(getAllFilters());
        mentorshipRequestService.getRequests(mentorshipRequestFilterDto);
        verify(mentorshipRequestRepository, times(1)).findAll();
    }

    @Test
    public void testGetRequestsWithAppropriateRequestSelection() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = getMentorshipRequestFilterDto(
                1L, 2L,
                "description", RequestStatus.PENDING);
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

    @Test
    public void testAcceptRequestWithNonExistingMentorshipRequest() {
        long requestId = 1L;

        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.empty());
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.acceptRequest(1L));
        assertEquals("Could not find Mentorship Request in database by id: " + requestId,
                exception.getMessage());
    }

    @Test
    public void testAcceptRequestWithValidatorExecutionVerification() {
        MentorshipRequest mentorshipRequest = getMentorshipRequest(1L, RequestStatus.ACCEPTED);

        when(mentorshipRequestRepository.findById(mentorshipRequest.getId()))
                .thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.acceptRequest(mentorshipRequest.getId());
        verify(mentorshipRequestValidator, times(1))
                .validateRequestStatusIsPending(mentorshipRequest.getStatus());
    }

    @Test
    public void testAcceptRequestWithRepositorySaveExecutionVerification() {
        MentorshipRequest mentorshipRequest = getMentorshipRequest(1L, RequestStatus.PENDING);

        when(mentorshipRequestRepository.findById(mentorshipRequest.getId()))
                .thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.acceptRequest(mentorshipRequest.getId());
        verify(mentorshipRequestRepository, times(1)).save(mentorshipRequest);
    }

    @Test
    public void testAcceptRequestWithToDtoExecution() {
        MentorshipRequest mentorshipRequest = getMentorshipRequest(1L, RequestStatus.PENDING);

        when(mentorshipRequestRepository.findById(mentorshipRequest.getId()))
                .thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.acceptRequest(mentorshipRequest.getId());
        verify(mentorshipRequestMapper, times(1)).toDto(mentorshipRequest);
    }

    private MentorshipRequestFilterDto getMentorshipRequestFilterDto(long requesterId, long receiverId,
                                                                     String description, RequestStatus requestStatus) {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto();
        mentorshipRequestFilterDto.setRequesterId(requesterId);
        mentorshipRequestFilterDto.setReceiverId(receiverId);
        mentorshipRequestFilterDto.setDescription(description);
        mentorshipRequestFilterDto.setStatus(requestStatus);
        return mentorshipRequestFilterDto;
    }

    private MentorshipRequestDto getMentorshipRequestDto(long requesterId, long receiverId,
                                                         LocalDateTime creationTime, String description) {
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setRequesterId(requesterId);
        mentorshipRequestDto.setReceiverId(receiverId);
        mentorshipRequestDto.setCreatedAt(creationTime);
        mentorshipRequestDto.setDescription(description);
        return mentorshipRequestDto;
    }

    private Stream<MentorshipRequestFilter> getAllFilters() {
        return Stream.of(
                new MentorshipRequestRequesterFilter(),
                new MentorshipRequestReceiverFilter(),
                new MentorshipRequestDescriptionFilter(),
                new MentorshipRequestStatusFilter()
        );
    }

    private MentorshipRequest getMentorshipRequest(long requestId, long requesterId, long receiverId,
                                                   String description, RequestStatus requestStatus) {
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setId(requestId);
        mentorshipRequest.setRequester(getUser(requesterId));
        mentorshipRequest.setReceiver(getUser(receiverId));
        mentorshipRequest.setDescription(description);
        mentorshipRequest.setStatus(requestStatus);
        return mentorshipRequest;
    }

    private MentorshipRequest getMentorshipRequest(long requesterId, long receiverId,
                                                   String description, RequestStatus requestStatus) {
        return getMentorshipRequest(0L, requesterId, receiverId, description, requestStatus);
    }

    private MentorshipRequest getMentorshipRequest(long requestId, RequestStatus requestStatus) {
        return getMentorshipRequest(requestId, 0L, 0L, null, requestStatus);
    }

    private User getUser(long userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }
}