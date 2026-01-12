package school.faang.user_service.service.mentorship;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.mentorship_request.MentorshipRequestFilter;
import school.faang.user_service.filter.mentorship_request.MentorshipRequestReceiverIdFilter;
import school.faang.user_service.filter.mentorship_request.MentorshipRequestRequesterIdFilter;
import school.faang.user_service.mapper.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceImplTest {
    private static final int MIN_MONTHS_BETWEEN = 3;
    @InjectMocks
    private MentorshipRequestServiceImpl mentorshipRequestService;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Spy
    private MentorshipRequestMapperImpl mentorshipRequestMapper;
    @Mock
    private UserContext userContext;

    private final MentorshipRequestFilter receiverIdFilter = new MentorshipRequestReceiverIdFilter();
    private final MentorshipRequestFilter requesterIdFilter = new MentorshipRequestRequesterIdFilter();

    @BeforeEach
    void setUp() {
        mentorshipRequestService = new MentorshipRequestServiceImpl(mentorshipRequestRepository,
                mentorshipRequestMapper, userContext, List.of(requesterIdFilter, receiverIdFilter)) {
        };
    }

    @Test
    void testCreateRequestToYourself() {
        CreateMentorshipRequestDto requestDto = new CreateMentorshipRequestDto("Description", 1L);
        long currentUserId = 1L;

        when(userContext.getUserId()).thenReturn(currentUserId);

        assertThrows(ForbiddenException.class, () -> mentorshipRequestService.create(requestDto));
    }

    @Test
    void testCreateRequestTooSoon() {
        long currentUserId = 1L;
        long mentorId = 2L;
        CreateMentorshipRequestDto requestDto = new CreateMentorshipRequestDto("Description", mentorId);
        MentorshipRequest previousRequest = createPreviousRequest(1L, currentUserId, mentorId,
                2, RequestStatus.ACCEPTED);

        when(userContext.getUserId()).thenReturn(currentUserId);
        when(mentorshipRequestRepository.findLatestRequest(currentUserId, mentorId))
                .thenReturn(Optional.of(previousRequest));

        assertThrows(DataValidationException.class, () -> mentorshipRequestService.create(requestDto));
    }

    @Test
    void testCreateRequestIsNotAccepted() {
        long currentUserId = 1L;
        long mentorId = 2L;
        CreateMentorshipRequestDto requestDto = new CreateMentorshipRequestDto("Description", mentorId);
        MentorshipRequest previousRequest = createPreviousRequest(1L, currentUserId, mentorId,
                MIN_MONTHS_BETWEEN, RequestStatus.REJECTED);

        when(userContext.getUserId()).thenReturn(currentUserId);
        when(mentorshipRequestRepository.findLatestRequest(currentUserId, mentorId))
                .thenReturn(Optional.of(previousRequest));

        assertThrows(ForbiddenException.class, () -> mentorshipRequestService.create(requestDto));
    }

    @Test
    void testCreate() {
        long currentUserId = 1L;
        long mentorId = 2L;
        MentorshipRequest createdRequest = createPreviousRequest(5L, currentUserId, mentorId,
                0, RequestStatus.PENDING);
        createdRequest.setDescription("Description");
        CreateMentorshipRequestDto requestDto = new CreateMentorshipRequestDto("Description", mentorId);
        MentorshipRequest previousRequest = createPreviousRequest(1L, currentUserId, mentorId,
                MIN_MONTHS_BETWEEN, RequestStatus.ACCEPTED);

        when(userContext.getUserId()).thenReturn(currentUserId);
        when(mentorshipRequestRepository.create(
                userContext.getUserId(), requestDto.mentorId(), requestDto.description()))
                .thenReturn(createdRequest);
        when(mentorshipRequestRepository.findLatestRequest(currentUserId, mentorId))
                .thenReturn(Optional.of(previousRequest));
        when(mentorshipRequestRepository.save(createdRequest)).thenReturn(createdRequest);

        MentorshipRequestDto actualDto = mentorshipRequestService.create(requestDto);

        MentorshipRequestDto previousDto = mentorshipRequestMapper.toMentorshipRequestDto(createdRequest);
        assertEquals(previousDto, actualDto);
    }

    @Test
    void testToMentorshipRequestDtoRequestNotFound() {
        long requestId = 1L;

        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class,
                () -> mentorshipRequestService.toMentorshipRequestDto(requestId));
    }

    @Test
    void testToMentorshipRequestDto() {
        long requestId = 1L;
        MentorshipRequest createdRequest = new MentorshipRequest();
        createdRequest.setId(requestId);
        createdRequest.setDescription("Description");

        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(createdRequest));
        MentorshipRequestDto expectedDto = mentorshipRequestMapper.toMentorshipRequestDto(createdRequest);

        MentorshipRequestDto actualDto = mentorshipRequestService.toMentorshipRequestDto(requestId);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void testFilters_Use() {
        MentorshipRequest mentorshipRequest1 = createMentorshipRequest(1L, 2L);
        MentorshipRequest mentorshipRequest2 = createMentorshipRequest(3L, 4L);

        when(mentorshipRequestRepository.findAll()).thenReturn(List.of(mentorshipRequest1, mentorshipRequest2));

        List<MentorshipRequestDto> result = mentorshipRequestService.getByFilters(
                new MentorshipRequestFilterDto(1L, 2L, null));
        assertEquals(1, result.size());
    }

    @Test
    void testAcceptRequestNotFound() {
        long requestId = 1L;

        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class,
                () -> mentorshipRequestService.accept(requestId));
    }

    @Test
    void testAcceptOnlyReceiver() {
        long requestId = 1L;
        long currentUserId = 2L;
        long receiverId = 4L;
        MentorshipRequest mentorshipRequest = createMentorshipRequest(requestId, receiverId);

        when(userContext.getUserId()).thenReturn(currentUserId);
        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(mentorshipRequest));

        assertThrows(ForbiddenException.class,
                () -> mentorshipRequestService.accept(requestId));
    }

    @Test
    void testAccepted() {
        long requestId = 1L;
        long currentUserId = 2L;
        long receiverId = 2L;
        MentorshipRequest mentorshipRequest = createMentorshipRequest(requestId, receiverId);

        when(userContext.getUserId()).thenReturn(currentUserId);
        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.accept(requestId);

        verify(mentorshipRequestRepository, times(1)).save(mentorshipRequest);
    }

    @Test
    void testRejected() {
        long requestId = 1L;
        long currentUserId = 2L;
        long receiverId = 2L;
        MentorshipRequest mentorshipRequest = createMentorshipRequest(requestId, receiverId);
        RejectionDto rejectionDto = new RejectionDto("Reason");

        when(userContext.getUserId()).thenReturn(currentUserId);
        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.reject(requestId, rejectionDto);

        verify(mentorshipRequestRepository, times(1)).save(mentorshipRequest);
    }

    @NotNull
    private  MentorshipRequest createPreviousRequest(Long id, long requesterId, long receiverId,
                                                     int monthsAgo, RequestStatus status) {
        MentorshipRequest previousRequest = createMentorshipRequest(requesterId, receiverId);
        previousRequest.setId(id);
        previousRequest.setCreatedAt(LocalDateTime.now().minusMonths(monthsAgo));
        previousRequest.setStatus(status);

        return previousRequest;
    }

    @NotNull
    private MentorshipRequest createMentorshipRequest(long requestId, long receiverId) {
        User user2 = new User();
        user2.setId(requestId);
        User user1 = new User();
        user1.setId(receiverId);
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(user2);
        mentorshipRequest.setReceiver(user1);

        return mentorshipRequest;
    }

}
