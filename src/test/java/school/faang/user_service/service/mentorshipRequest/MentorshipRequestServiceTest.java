package school.faang.user_service.service.mentorshipRequest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshipRequest.RejectionDto;
import school.faang.user_service.dto.mentorshipRequest.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorshipRequest.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.util.mentorshipRequest.exception.NoRequestsException;
import school.faang.user_service.util.mentorshipRequest.exception.RequestIsAlreadyAcceptedException;
import school.faang.user_service.util.mentorshipRequest.exception.RequestIsAlreadyRejectedException;
import school.faang.user_service.util.mentorshipRequest.validator.FilterRequestStatusValidator;
import school.faang.user_service.util.mentorshipRequest.validator.MentorshipRequestValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private FilterRequestStatusValidator filterRequestStatusValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;

    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRequestMentorship_ShouldCreateMentorship() {
        String description = "description";
        long requesterId = 1;
        long receiverId = 2;

        User requester = new User();
        User receiver = new User();
        MentorshipRequestDto mentorshipRequestDto =
                new MentorshipRequestDto(description, requesterId, receiverId);
        MentorshipRequest mentorshipRequest =
                new MentorshipRequest();

        requester.setId(requesterId);
        receiver.setId(receiverId);

        mentorshipRequest.setDescription(description);
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);

        Mockito.when(mentorshipRequestMapper.toEntity(mentorshipRequestDto, mentorshipRequestService))
                .thenReturn(mentorshipRequest);
        Mockito.doNothing().when(mentorshipRequestValidator).validate(Mockito.any(), Mockito.any(), Mockito.any());

        mentorshipRequestService.requestMentorship(mentorshipRequestDto);

        Mockito.verify(mentorshipRequestRepository, Mockito.times(1))
                .save(mentorshipRequest);
    }

    @Test
    void testGetRequests_AllFiltersAreUsed_ShouldReturnListOfRequestDtos() {
        String description = "description";
        long requesterId = 1;
        long receiverId = 2;
        String status = "PENDING";

        User requester = new User();
        User receiver = new User();
        RequestFilterDto requestFilterDto =
                new RequestFilterDto(description, requesterId, receiverId, status);
        MentorshipRequest mentorshipRequest =
                new MentorshipRequest();

        requester.setId(1);
        requester.setId(2);

        mentorshipRequest.setDescription(description);
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setStatus(RequestStatus.PENDING);

        Mockito
                .when(mentorshipRequestMapper.toEntity(requestFilterDto, mentorshipRequestService, filterRequestStatusValidator))
                .thenReturn(mentorshipRequest);

        Mockito
                .when(mentorshipRequestRepository.findAll())
                .thenReturn(getListOfRequests(description, requester, receiver));

        Assertions.assertEquals(1, mentorshipRequestService.getRequests(requestFilterDto).size());
    }

    @Test
    void testGetRequests_NotAllFiltersAreUsed_ShouldReturnListOfRequestDtos() {
        String description = "description";

        RequestFilterDto requestFilterDto =
                new RequestFilterDto();
        MentorshipRequest mentorshipRequest =
                new MentorshipRequest();

        requestFilterDto.setDescription(description);

        mentorshipRequest.setDescription(description);

        Mockito
                .when(mentorshipRequestMapper.toEntity(requestFilterDto, mentorshipRequestService, filterRequestStatusValidator))
                .thenReturn(mentorshipRequest);

        Mockito
                .when(mentorshipRequestRepository.findAll())
                .thenReturn(getListOfRequests(description));

        Assertions.assertEquals(1, mentorshipRequestService.getRequests(requestFilterDto).size());
    }

    @Test
    void findUserById() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));

        mentorshipRequestService.findUserById(Mockito.anyLong());

        Mockito.verify(userRepository, Mockito.times(1)).findById(Mockito.anyLong());
    }

    private List<MentorshipRequest> getListOfRequests(String description) {
        MentorshipRequest mentorshipRequest1 =
                new MentorshipRequest();

        mentorshipRequest1.setDescription(description);

        MentorshipRequest mentorshipRequest2 =
                new MentorshipRequest();

        mentorshipRequest2.setDescription("descr");

        return List.of(
                mentorshipRequest1,
                mentorshipRequest2
        );
    }

    private List<MentorshipRequest> getListOfRequests(String description,
                                                      User requester,
                                                      User receiver) {
        MentorshipRequest mentorshipRequest1 =
                new MentorshipRequest();

        mentorshipRequest1.setDescription(description);
        mentorshipRequest1.setRequester(requester);
        mentorshipRequest1.setReceiver(receiver);
        mentorshipRequest1.setStatus(RequestStatus.PENDING);

        MentorshipRequest mentorshipRequest2 =
                new MentorshipRequest();

        mentorshipRequest2.setDescription("descr");
        mentorshipRequest2.setRequester(requester);
        mentorshipRequest2.setReceiver(receiver);
        mentorshipRequest2.setStatus(RequestStatus.PENDING);

        return List.of(
                mentorshipRequest1,
                mentorshipRequest2
        );
    }

    @Test
    void testAcceptRequest_InputsAreValid_ShouldComplete() {
        long id = 2;
        MentorshipRequest mentorshipRequest =
                new MentorshipRequest();
        User receiver = new User();
        User requester = new User();
        User testUser = new User();

        mentorshipRequest.setId(id);
        receiver.setId(1);
        requester.setId(2);
        receiver.setMentees(new ArrayList<>());
        requester.setMentors(new ArrayList<>());
        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setStatus(RequestStatus.PENDING);

        Mockito.when(mentorshipRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.acceptRequest(id);

        Assertions.assertTrue(receiver.getMentees().contains(requester));
        Assertions.assertTrue(requester.getMentors().contains(receiver));
        Assertions.assertEquals(RequestStatus.ACCEPTED, mentorshipRequest.getStatus());
        Mockito.verify(mentorshipRequestRepository, Mockito.times(1)).save(mentorshipRequest);
    }

    @Test
    void testAcceptRequest_InputsAreInvalid_ShouldThrowException() {
        long id = 2;
        MentorshipRequest mentorshipRequest =
                new MentorshipRequest();
        User receiver = new User();
        User requester = new User();

        mentorshipRequest.setId(id);
        receiver.setId(1);
        requester.setId(2);
        mentorshipRequest.setReceiver(receiver);
        receiver.setMentees(new ArrayList<>(List.of(requester)));
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);

        Mockito.when(mentorshipRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mentorshipRequest));

        Assertions.assertThrows(RequestIsAlreadyAcceptedException.class,
                () -> mentorshipRequestService.acceptRequest(id));
    }

    @Test
    void testRejectRequest_InputsAreInvalid_ShouldThrowException() {
        long id = 1;
        Mockito.when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(NoRequestsException.class,
                () -> mentorshipRequestService.rejectRequest(id, new RejectionDto("reason")));
    }

    @Test
    void testRejectRequest_InputsAreValidAndRequestWasAccepted_ShouldComplete() {
        long id = 1;
        MentorshipRequest foundRequest = new MentorshipRequest();
        User receiver = new User();
        User requester = new User();

        foundRequest.setId(id);
        receiver.setId(1);
        requester.setId(2);
        receiver.setMentees(new ArrayList<>(List.of(requester)));
        requester.setMentors(new ArrayList<>(List.of(receiver)));
        foundRequest.setReceiver(receiver);
        foundRequest.setRequester(requester);
        foundRequest.setStatus(RequestStatus.ACCEPTED);

        Mockito.when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.of(foundRequest));

        mentorshipRequestService.rejectRequest(id, new RejectionDto("reason"));

        Assertions.assertFalse(receiver.getMentees().contains(requester));
        Assertions.assertFalse(requester.getMentors().contains(receiver));
        Assertions.assertEquals(RequestStatus.REJECTED, foundRequest.getStatus());
        Mockito.verify(mentorshipRequestRepository, Mockito.times(1)).save(foundRequest);
    }

    @Test
    void testRejectRequest_InputsAreValidAndRequestWasPending_ShouldComplete() {
        long id = 1;
        MentorshipRequest foundRequest = new MentorshipRequest();
        User receiver = new User();
        User requester = new User();

        foundRequest.setId(id);
        receiver.setId(1);
        requester.setId(2);
        receiver.setMentees(new ArrayList<>());
        requester.setMentors(new ArrayList<>());
        foundRequest.setReceiver(receiver);
        foundRequest.setRequester(requester);
        foundRequest.setStatus(RequestStatus.PENDING);

        Mockito.when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.of(foundRequest));

        mentorshipRequestService.rejectRequest(id, new RejectionDto("reason"));

        Assertions.assertFalse(receiver.getMentees().contains(requester));
        Assertions.assertFalse(requester.getMentors().contains(receiver));
        Assertions.assertEquals(RequestStatus.REJECTED, foundRequest.getStatus());
        Mockito.verify(mentorshipRequestRepository, Mockito.times(1)).save(foundRequest);
    }

    @Test
    void testRejectRequest_RequestIsAlreadyRejected_ShouldThrowException() {
        long id = 1;
        MentorshipRequest foundRequest = new MentorshipRequest();
        foundRequest.setStatus(RequestStatus.REJECTED);
        Mockito.when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.of(foundRequest));

        Assertions.assertThrows(RequestIsAlreadyRejectedException.class,
                () -> mentorshipRequestService.rejectRequest(id, new RejectionDto("reason")));
    }
}

