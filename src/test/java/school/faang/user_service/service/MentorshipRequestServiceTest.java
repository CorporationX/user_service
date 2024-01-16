package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class MentorshipRequestServiceTest {
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;

    @InjectMocks
    MentorshipRequestService mentorshipRequestService;


    @Test
    public void testIsReceiverExistsIsInvalid() {
        long requesterId = 1L;
        long receiverId = 2L;

        Mockito.when(userRepository.existsById(receiverId)).thenReturn(false);
        Mockito.when(userRepository.existsById(requesterId)).thenReturn(true);

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(new MentorshipRequestDto(requesterId, receiverId, "String")
        ));

        Mockito.verify(userRepository, Mockito.times(1))
                .existsById(receiverId);
    }

    @Test
    public void testIsRequesterExistsIsInvalid() {
        long requesterId = 1L;
        long receiverId = 2L;

        Mockito.when(userRepository.existsById(requesterId)).thenReturn(false);
        Mockito.when(userRepository.existsById(receiverId)).thenReturn(true);

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(new MentorshipRequestDto(requesterId, receiverId, "String")
                ));

        Mockito.verify(userRepository, Mockito.times(1))
                .existsById(requesterId);
    }

    @Test
    public void testIsNotRequestToYourselfIsInvalid() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);

        Assert.assertThrows(
          IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(new MentorshipRequestDto(1L, 1L, "description"))
        );

        Mockito.verify(userRepository, Mockito.times(1))
                .existsById(1L);
    }

    @Test
    public void testIsMoreThanThreeMonthsIsInvalid() {
        Long requesterId = 1L;
        Long receiverId = 2L;

        Mockito.when(userRepository.existsById(requesterId)).thenReturn(true);
        Mockito.when(userRepository.existsById(receiverId)).thenReturn(true);

        MentorshipRequest request = new MentorshipRequest();
        request.setUpdatedAt(LocalDateTime.now().minusMonths(2));
        Mockito.when(mentorshipRequestRepository.findLatestRequest(requesterId, receiverId)).thenReturn(Optional.of(request));

        Assert.assertThrows(
          IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(new MentorshipRequestDto(requesterId, receiverId, "description"))
        );
    }

    @Test
    public void testRequestMentorship() {
        Long requesterId = 1L;
        Long receiverId = 2L;

        Mockito.when(userRepository.existsById(requesterId)).thenReturn(true);
        Mockito.when(userRepository.existsById(receiverId)).thenReturn(true);

        mentorshipRequestService.requestMentorship(new MentorshipRequestDto(requesterId, receiverId, "description"));
        Mockito.verify(mentorshipRequestRepository, Mockito.times(1))
                .create(requesterId, receiverId, "description");
    }

    @Test
    public void testRequestExistsIsInvalid() {
        long requestId = 1L;
        Mockito.when(mentorshipRequestRepository.existsById(requestId)).thenReturn(false);

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.rejectRequest(requestId, new RejectionDto("Reason"))
        );
    }

    @Test
    public void testReasonIsGiven() {
        long requestId = 1L;
        String reason = "Reason";

        Mockito.when(mentorshipRequestRepository.existsById(requestId)).thenReturn(true);

        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(requestId).orElseThrow();

        mentorshipRequestService.rejectRequest(requestId, new RejectionDto(reason));
        Mockito.verify(mentorshipRequest, Mockito.times(1))
                .setRejectionReason(reason);
    }

    @Test
    public void testStatusChanged() {
        long requestId = 1L;

        Mockito.when(mentorshipRequestRepository.existsById(requestId)).thenReturn(true);

        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setId(requestId);
        mentorshipRequest.setStatus(RequestStatus.PENDING);

        Mockito.when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(mentorshipRequest));
        mentorshipRequestService.rejectRequest(requestId, new RejectionDto("Reason"));

        Assert.assertEquals(RequestStatus.REJECTED, mentorshipRequest.getStatus());
    }

    @Test
    public void testIsRequestExistsInDbIsInvalid() {
        long requestId = 1L;
        Mockito.when(mentorshipRequestRepository.existsById(requestId)).thenReturn(false);

        Assert.assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestService.acceptRequest(requestId));
    }

    @Test
    public void testRequesterNotContainsMentorIsInvalid() {
        long requestId = 1L;
        long requesterId = 1L;
        long receiverId = 2L;
        Mockito.when(mentorshipRequestRepository.existsById(requestId)).thenReturn(true);
        Mockito.when(userRepository.existsById(requesterId)).thenReturn(true);
        Mockito.when(userRepository.existsById(receiverId)).thenReturn(true);

        boolean condition1 = mentorshipRequestService.getMentorsAndUsers().containsKey(receiverId);
        boolean condition2 = mentorshipRequestService.getMentorsAndUsers().get(receiverId).contains(requesterId);

        Assert.assertThrows(IllegalArgumentException.class, () -> {
            if (!condition1 || !condition2) {
                throw new IllegalArgumentException("The user is already the sender's mentor");
            }
        });
    }

    @Test
    public void testAddToList() {
        long requestId = 1L;
        long requesterId = 1L;
        long receiverId = 2L;
        Mockito.when(mentorshipRequestRepository.existsById(requestId)).thenReturn(true);
        Mockito.when(userRepository.existsById(requesterId)).thenReturn(true);
        Mockito.when(userRepository.existsById(receiverId)).thenReturn(true);

        mentorshipRequestService.acceptRequest(requestId);
        Mockito.verify(mentorshipRequestService.getMentorsAndUsers(), Mockito.times(1))
                .put(List.of(receiverId), List.of(requesterId));
    }

    @Test
    public void testStatusChangesToAccepted() {
        long requestId = 1L;

        Mockito.when(mentorshipRequestRepository.existsById(requestId)).thenReturn(true);

        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setId(requestId);
        mentorshipRequest.setStatus(RequestStatus.PENDING);

        Mockito.when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(mentorshipRequest));
        mentorshipRequestService.acceptRequest(requestId);

        Assert.assertEquals(RequestStatus.ACCEPTED, mentorshipRequest.getStatus());
    }
}
