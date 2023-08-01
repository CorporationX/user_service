package school.faang.user_service.service.mentorship;

import org.mockito.Spy;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import school.faang.user_service.entity.User;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import jakarta.persistence.EntityNotFoundException;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.dto.event.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapperImpl;
import school.faang.user_service.validation.mentorship.MentorshipRequestValidator;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.util.List;
import java.util.Optional;

import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {

    @Spy
    private MentorshipRequestMapperImpl mentorshipRequestMapper;
    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    private Long requestId;
    private Long badRequestId;

    private RejectionDto rejectionDto;

    private MentorshipRequestDto mentorshipRequestDto;
    private MentorshipRequestDto badMentorshipRequestDto;

    private RequestFilterDto requestFilterDto;

    @BeforeEach
    void setUp() {
        mentorshipRequestDto = MentorshipRequestDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .description("Wanna code like you bruh")
                .build();

        badMentorshipRequestDto = MentorshipRequestDto.builder() // Because requesterId == receiverId
                .requesterId(1L)
                .receiverId(1L)
                .build();

        requestFilterDto = RequestFilterDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .description("Wanna be a good coder")
                .requestStatus(RequestStatus.ACCEPTED)
                .build();

        requestId = 1L;
        badRequestId = -1_234_567_890L;

        rejectionDto = new RejectionDto("Rejection example");
    }

    @Test
    @DisplayName("Mentorship request - positive scenario")
    void testRequestMentorshipIsOk() {
        Assertions.assertDoesNotThrow(() -> mentorshipRequestService.requestMentorship(mentorshipRequestDto));
    }

    @Test
    @DisplayName("Mentorship request with requester & receiver being same person")
    void testRequestMentorshipBadRequest() {
        doThrow(IllegalArgumentException.class).when(mentorshipRequestValidator).validate(any());
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(badMentorshipRequestDto));
    }

    @Test
    @DisplayName("Get requests - positive scenario")
    void testGetRequestsIsOk() {
        Assertions.assertDoesNotThrow(() -> mentorshipRequestService.getRequests(requestFilterDto));
    }

    @Test
    @DisplayName("Get requests - filter request is null")
    void testGetRequestsBadRequest() {
        when(mentorshipRequestService.getRequests(null)).thenThrow(NullPointerException.class);
        Assertions.assertThrows(NullPointerException.class, () -> mentorshipRequestService.getRequests(null));
    }

    @Test
    @DisplayName("Accept request - positive scenario")
    void testAcceptRequestIsOk() {
        User receiver = User.builder()
                .id(1L)
                .build();

        User requester = User.builder()
                .id(2L)
                .mentors(new ArrayList<>())
                .build();

        MentorshipRequest mentorshipRequest = MentorshipRequest.builder()
                .id(1L)
                .receiver(receiver)
                .requester(requester)
                .build();

        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(mentorshipRequest));
        MentorshipRequestDto accepted = mentorshipRequestService.acceptRequest(requestId);
        assertEquals(RequestStatus.ACCEPTED, accepted.getStatus());
        assertEquals(1, requester.getMentors().size());
        assertEquals(receiver, requester.getMentors().get(0));
    }

    @Test
    @DisplayName("Accept request - mentorship request not found")
    void testAcceptRequestThrowsRequestNotFoundException() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> mentorshipRequestService.acceptRequest(badRequestId));
    }

    @Test
    @DisplayName("Reject request - positive scenario")
    void testRejectRequestPositiveIsOk() {
        User receiver = User.builder()
                .id(1L)
                .build();

        User requester = User.builder()
                .id(2L)
                .mentors(List.of())
                .build();

        MentorshipRequest mentorshipRequest = MentorshipRequest.builder()
                .id(1L)
                .receiver(receiver)
                .requester(requester)
                .build();

        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(mentorshipRequest));
        Assertions.assertDoesNotThrow(() -> mentorshipRequestService.rejectRequest(requestId, rejectionDto));
        Assertions.assertEquals(mentorshipRequest.getStatus(), RequestStatus.REJECTED);
        Assertions.assertFalse(mentorshipRequest.getRejectionReason().isBlank());
    }

    @Test
    @DisplayName("Reject request - mentorship request not found")
    void testRejectRequestThrowsRequestNotFoundException() {
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> mentorshipRequestService.rejectRequest(badRequestId, rejectionDto));
    }
}