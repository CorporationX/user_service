package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapstruct.MentorshipRequestMapper;
import school.faang.user_service.publisher.MentorshipRequestedEventPublisher;
import school.faang.user_service.publisher.MentorshipStartEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {

    @Test
    public void testRequestMentorship_Success() {
        User requester = createUser(1L);
        User receiver = createUser(2L);
        MentorshipRequest mentorshipRequest = createMentorshipRequest(requester, receiver);
        MentorshipRequestDto mentorshipRequestDto = createMentorshipRequestDto(requester, receiver);

        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(mentorshipRequestRepository.save(any(MentorshipRequest.class))).thenReturn(mentorshipRequest);
        when(mentorshipRequestMapper.mapToDto(mentorshipRequest)).thenReturn(mentorshipRequestDto);

        MentorshipRequestDto result = mentorshipRequestService.requestMentorship(mentorshipRequestDto);

        assertNotNull(result);
        assertEquals(mentorshipRequestDto, result);
        verify(mentorshipRequestRepository, times(1)).save(any(MentorshipRequest.class));
        verify(mentorshipRequestedEventPublisher).publish(any());
    }

    @Test
    public void testRequestMentorship_SelfRequest_ThrowsException() {
        User requester = createUser(1L);
        MentorshipRequestDto mentorshipRequestDto = createMentorshipRequestDto(requester, requester);
        mentorshipRequestDto.setReceiverId(requester.getId());

        assertThrows(IllegalArgumentException.class, () -> {
            mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        });
    }

    @Test
    public void testGetRequests_ByStatus() {
        User requester = createUser(1L);
        User receiver = createUser(2L);
        MentorshipRequest mentorshipRequest = createMentorshipRequest(requester, receiver);
        MentorshipRequestDto mentorshipRequestDto = createMentorshipRequestDto(requester, receiver);

        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setStatus(RequestStatus.PENDING);

        when(mentorshipRequestRepository.findAll()).thenReturn(List.of(mentorshipRequest));
        when(mentorshipRequestMapper.mapToDto(mentorshipRequest)).thenReturn(mentorshipRequestDto);

        List<MentorshipRequestDto> result = mentorshipRequestService.getRequests(filterDto);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mentorshipRequestDto, result.get(0));
    }

    @Test
    public void testAcceptMentorship_Success() {
        User requester = createUser(1L);
        User receiver = createUser(2L);
        MentorshipRequest mentorshipRequest = createMentorshipRequest(requester, receiver);
        mentorshipRequest.setDescription("Sample description");

        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));
        when(userRepository.save(requester)).thenReturn(requester);

        MentorshipRequestDto dto = new MentorshipRequestDto(
                1L,
                mentorshipRequest.getRequester().getId(),
                mentorshipRequest.getReceiver().getId(),
                mentorshipRequest.getDescription(),
                RequestStatus.ACCEPTED,
                null
        );

        when(mentorshipRequestMapper.mapToDto(mentorshipRequest)).thenReturn(dto);

        MentorshipRequestDto result = mentorshipRequestService.acceptMentorship(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(RequestStatus.ACCEPTED, result.getStatus());
        assertTrue(requester.getMentors().contains(receiver));
        verify(mentorshipStartEventPublisher).publish(any());
    }

    @Test
    public void testAcceptMentorship_AlreadyMentor_ThrowsException() {
        User requester = createUser(1L);
        User receiver = createUser(2L);
        requester.getMentors().add(receiver);
        MentorshipRequest mentorshipRequest = createMentorshipRequest(requester, receiver);

        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));

        assertThrows(IllegalArgumentException.class, () -> {
            mentorshipRequestService.acceptMentorship(1L);
        });
    }

    @Test
    public void testRejectRequest_Success() {
        User requester = createUser(1L);
        User receiver = createUser(2L);
        MentorshipRequest mentorshipRequest = createMentorshipRequest(requester, receiver);
        MentorshipRequestDto mentorshipRequestDto = createMentorshipRequestDto(requester, receiver);

        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setReason("Test Reason");

        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));
        when(mentorshipRequestMapper.mapToDto(mentorshipRequest)).thenReturn(mentorshipRequestDto);

        MentorshipRequestDto result = mentorshipRequestService.rejectRequest(1L, rejectionDto);

        assertNotNull(result);
        assertEquals(mentorshipRequestDto, result);
        assertEquals(RequestStatus.REJECTED, mentorshipRequest.getStatus());
        assertEquals("Test Reason", mentorshipRequest.getRejectionReason());
    }

    @Spy
    private MentorshipRequestMapper mentorshipRequestMapper = Mappers.getMapper(MentorshipRequestMapper.class);

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MentorshipRequestedEventPublisher mentorshipRequestedEventPublisher;

    @Mock
    private MentorshipStartEventPublisher mentorshipStartEventPublisher;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    private User createUser(Long id) {
        return User.builder()
                .id(id)
                .sentMentorshipRequests(new ArrayList<>())
                .mentors(new ArrayList<>())
                .build();
    }

    private MentorshipRequest createMentorshipRequest(User requester, User receiver) {
        return MentorshipRequest.builder()
                .id(1L)
                .description("Test Description")
                .requester(requester)
                .receiver(receiver)
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private MentorshipRequestDto createMentorshipRequestDto(User requester, User receiver) {
        return MentorshipRequestDto.builder()
                .requesterId(requester.getId())
                .receiverId(receiver.getId())
                .description("Test Description")
                .status(RequestStatus.PENDING)
                .build();
    }
}
