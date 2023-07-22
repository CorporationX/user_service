package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.filter.MentorshipFilter;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.volidator.mentorship.MentorshipRequestValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MentorshipRequestServiceTest_RequestMentorship {
    @Mock
    private MentorshipFilter mentorshipFilter;
    @Mock
    private UserService userService;
    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;
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
        Mockito.when(userService.findUserById(1L))
                .thenReturn(Optional.of(requester));
        Mockito.when(userService.findUserById(2L))
                .thenReturn(Optional.of(receiver));

        mentorshipRequestService.requestMentorship(mentorshipRequestDto);

        Mockito.verify(userService, Mockito.times(1)).findUserById(requester.getId());
        Mockito.verify(userService, Mockito.times(1)).findUserById(receiver.getId());
        Mockito.verify(mentorshipRequestValidator, Mockito.times(1)).requestValidate(Optional.of(requester), Optional.of(receiver));
        Mockito.verify(mentorshipRequestMapper, Mockito.times(1)).toEntity(mentorshipRequestDto);
        Mockito.verify(mentorshipRequestRepository, Mockito.times(1)).save(mentorshipRequestMapper.toEntity(mentorshipRequestDto));
    }

    @Test
    public void testGetRequests() {
        RequestFilterDto filter = new RequestFilterDto(description, requester.getId(), receiver.getId(), RequestStatus.PENDING);
        List<MentorshipRequest> mentorshipRequests = new ArrayList<>();
        Mockito.when(mentorshipRequestRepository.getAllRequests(filter.getDescription(),
                        filter.getRequesterId(),
                        filter.getReceiverId(),
                        filter.getStatus()))
                .thenReturn(mentorshipRequests);

        mentorshipRequestService.getRequests(filter);
        Mockito.verify(mentorshipRequestRepository, Mockito.times(1)).getAllRequests(filter.getDescription(),
                filter.getRequesterId(),
                filter.getReceiverId(),
                filter.getStatus());
        Mockito.verify(mentorshipFilter, Mockito.times(1)).filterRequests(mentorshipRequests,filter);
    }

    @Test
    public void testAcceptRequest() {
        long id = mentorshipRequest.getId();

        Mockito.when(mentorshipRequestRepository.findById(id))
                .thenReturn(Optional.of(mentorshipRequest));
        Mockito.when(mentorshipRequestValidator.acceptRequestValidator(Optional.of(mentorshipRequest)))
                .thenReturn(mentorshipRequest);

        mentorshipRequestService.acceptRequest(id);

        Mockito.verify(mentorshipRequestRepository, Mockito.times(1)).findById(id);
        Mockito.verify(mentorshipRequestValidator, Mockito.times(1)).acceptRequestValidator(Optional.of(mentorshipRequest));
        Mockito.verify(userService, Mockito.times(1)).saveUser(mentorshipRequest.getRequester());
        Mockito.verify(userService, Mockito.times(1)).saveUser(mentorshipRequest.getReceiver());
        Mockito.verify(mentorshipRequestRepository, Mockito.times(1)).save(mentorshipRequest);
    }

    @Test
    public void testRejectRequest() {
        long id = mentorshipRequest.getId();
        RejectionDto rejectionDto = new RejectionDto("anyReason");

        Mockito.when(mentorshipRequestRepository.findById(id))
                .thenReturn(Optional.of(mentorshipRequest));
        Mockito.when(mentorshipRequestValidator.rejectRequestValidator(Optional.of(mentorshipRequest)))
                .thenReturn(mentorshipRequest);

        mentorshipRequestService.rejectRequest(id,rejectionDto);
        Mockito.verify(mentorshipRequestRepository, Mockito.times(1)).findById(id);
        Mockito.verify(mentorshipRequestValidator, Mockito.times(1)).rejectRequestValidator(Optional.of(mentorshipRequest));
    }
}
