package school.faang.user_service.services.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.volidate.mentorship.MentorshipRequestValidator;

import java.util.ArrayList;
import java.util.Optional;

public class MentorshipRequestServiceTest_RequestMentorship {
    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;
    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;
    private MentorshipRequestDto mentorshipRequestDto;
    private MentorshipRequest mentorshipRequest;
    private User requester = new User();
    private User receiver = new User();
    private String description = "description";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        long mentorshipRequestId = 0L;
        long requesterId = 1L;
        long receiverId = 2L;
        mentorshipRequestDto =
                new MentorshipRequestDto(0, description, requesterId, receiverId);

        mentorshipRequest =
                new MentorshipRequest();

        requester.setId(requesterId);
        requester.setMentors(new ArrayList<>());
        receiver.setId(receiverId);
        receiver.setMentees(new ArrayList<>());

        mentorshipRequest.setId(mentorshipRequestId);
        mentorshipRequest.setDescription(description);
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setStatus(RequestStatus.PENDING);
    }

    @Test
    public void testRequestMentorship() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(requester));
        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.ofNullable(receiver));

        mentorshipRequestService.requestMentorship(mentorshipRequestDto);

        Mockito.verify(userRepository, Mockito.times(1)).findById(requester.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(receiver.getId());
        Mockito.verify(mentorshipRequestMapper, Mockito.times(1)).toEntity(mentorshipRequestDto);
        Mockito.verify(mentorshipRequestValidator, Mockito.times(1)).requestValidate(requester, receiver);
        Mockito.verify(mentorshipRequestRepository, Mockito.times(1)).save(mentorshipRequestMapper.toEntity(mentorshipRequestDto));
    }

    @Test
    public void testGetRequests() {
        Mockito.when(mentorshipRepository.findById(receiver.getId()))
                .thenReturn(Optional.ofNullable(receiver));

        mentorshipRequestService.getRequests(receiver.getId());
        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(receiver.getId());
    }

    @Test
    public void testAcceptRequest() {
        long id = mentorshipRequest.getId();
        Mockito.when(mentorshipRequestRepository.findById(id))
                .thenReturn(Optional.ofNullable(mentorshipRequest));

        mentorshipRequestService.acceptRequest(id);
        Mockito.verify(mentorshipRequestRepository, Mockito.times(1)).findById(id);
        Mockito.verify(mentorshipRequestValidator, Mockito.times(1)).acceptRequestValidator(mentorshipRequest);
        Mockito.verify(userRepository, Mockito.times(1)).save(mentorshipRequest.getRequester());
        Mockito.verify(userRepository, Mockito.times(1)).save(mentorshipRequest.getReceiver());
        Mockito.verify(mentorshipRequestRepository, Mockito.times(1)).save(mentorshipRequest);
    }

    @Test
    public void testRejectRequest() {
        long id = mentorshipRequest.getId();
        RejectionDto rejectionDto = new RejectionDto("anyReason");

        Mockito.when(mentorshipRequestRepository.findById(id))
                .thenReturn(Optional.ofNullable(mentorshipRequest));

        mentorshipRequestService.rejectRequest(id,rejectionDto);
        Mockito.verify(mentorshipRequestRepository, Mockito.times(1)).findById(id);
        Mockito.verify(mentorshipRequestValidator, Mockito.times(1)).rejectRequestValidator(mentorshipRequest);
    }
}
