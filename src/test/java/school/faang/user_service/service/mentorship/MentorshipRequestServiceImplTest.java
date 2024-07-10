package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceImplTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;

    @InjectMocks
    private MentorshipRequestServiceImpl mentorshipRequestService;

    private User requester;
    private User receiver;
    private MentorshipRequest mentorshipRequest;
    private MentorshipRequestDto mentorshipRequestDto;
    private RequestFilterDto filter;

    @BeforeEach
    void setUp() {
        requester = new User();
        requester.setId(1L);
        receiver = new User();
        receiver.setId(2L);

        mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setDescription("Valid description");
        mentorshipRequest.setCreatedAt(LocalDateTime.now());

        mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setRequesterId(requester.getId());
        mentorshipRequestDto.setReceiverId(receiver.getId());
        mentorshipRequestDto.setDescription("Valid description");

        filter = new RequestFilterDto();
        filter.setDescription("test");
        filter.setRequesterId(1L);
        filter.setResponderId(2L);
        filter.setStatus(RequestStatus.PENDING);
    }

    @Test
    void requestMentorship_shouldSaveRequest_whenValid() {
        when(userRepository.findAllById(any())).thenReturn(List.of(requester, receiver));
        when(mentorshipRequestRepository.findLatestRequestByRequester(requester.getId())).thenReturn(Optional.empty());
        when(mentorshipRequestMapper.toEntity(any(MentorshipRequestDto.class))).thenReturn(mentorshipRequest);
        when(mentorshipRequestRepository.save(any(MentorshipRequest.class))).thenReturn(mentorshipRequest);
        when(mentorshipRequestMapper.toDto(any(MentorshipRequest.class))).thenReturn(mentorshipRequestDto);

        MentorshipRequestDto result = mentorshipRequestService.requestMentorship(mentorshipRequestDto);

        assertEquals(mentorshipRequestDto, result);
        verify(mentorshipRequestRepository, times(1)).save(mentorshipRequest);
    }

    @Test
    void acceptRequest_shouldThrowException_whenRequestNotFound() {
        when(mentorshipRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> mentorshipRequestService.acceptRequest(1L));

        assertEquals("Mentorship request with id 1 doesnt exist", exception.getMessage());
    }

    @Test
    void acceptRequest_shouldThrowException_whenRequestAlreadyAccepted() {
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);

        when(mentorshipRequestRepository.findById(anyLong())).thenReturn(Optional.of(mentorshipRequest));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.acceptRequest(1L));

        assertEquals("Mentorship request with id 1 already accepted", exception.getMessage());
    }

    @Test
    void acceptRequest_shouldThrowException_whenMentorshipAlreadyExists() {
        mentorshipRequest.setStatus(RequestStatus.PENDING);

        when(mentorshipRequestRepository.findById(anyLong())).thenReturn(Optional.of(mentorshipRequest));
        when(mentorshipRepository.findByMentorAndMentee(receiver.getId(), requester.getId())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.acceptRequest(1L));

        assertEquals("Mentorship already exists", exception.getMessage());
    }

    @Test
    void rejectRequest_shouldThrowException_whenRequestNotFound() {
        when(mentorshipRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> mentorshipRequestService.rejectRequest(1L, new RejectionDto()));

        assertEquals("Mentorship request with id 1 doesnt exist", exception.getMessage());
    }

    @Test
    void rejectRequest_shouldThrowException_whenRequestAlreadyAccepted() {
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);

        when(mentorshipRequestRepository.findById(anyLong())).thenReturn(Optional.of(mentorshipRequest));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.rejectRequest(1L, new RejectionDto()));

        assertEquals("Mentorship request with id 1 already accepted", exception.getMessage());
    }

    @Test
    void rejectRequest_shouldThrowException_whenRequestAlreadyRejected() {
        mentorshipRequest.setStatus(RequestStatus.REJECTED);

        when(mentorshipRequestRepository.findById(anyLong())).thenReturn(Optional.of(mentorshipRequest));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.rejectRequest(1L, new RejectionDto()));

        assertEquals("Mentorship request with id 1 already rejected", exception.getMessage());
    }

    @Test
    void getRequests_shouldReturnFilteredRequests() {
        MentorshipRequest request1 = new MentorshipRequest();
        MentorshipRequest request2 = new MentorshipRequest();
        MentorshipRequestDto requestDto1 = new MentorshipRequestDto();
        MentorshipRequestDto requestDto2 = new MentorshipRequestDto();

        when(mentorshipRequestRepository.findAllByFilter(anyString(), anyLong(), anyLong(), anyInt())).thenReturn(List.of(request1, request2));
        when(mentorshipRequestMapper.toDto(request1)).thenReturn(requestDto1);
        when(mentorshipRequestMapper.toDto(request2)).thenReturn(requestDto2);

        List<MentorshipRequestDto> result = mentorshipRequestService.getRequests(filter);

        assertEquals(2, result.size());
        verify(mentorshipRequestRepository, times(1)).findAllByFilter(anyString(), anyLong(), anyLong(), anyInt());
    }
}
