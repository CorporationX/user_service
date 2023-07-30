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
        MentorshipRequestDto requestDto = buildRequestDto();
        MentorshipRequest request = buildRequest();

        Mockito.when(mentorshipRequestMapper.toEntity(requestDto, mentorshipRequestService))
                .thenReturn(request);
        Mockito.doNothing().when(mentorshipRequestValidator).validate(Mockito.any(), Mockito.any(), Mockito.any());

        mentorshipRequestService.requestMentorship(requestDto);

        Mockito.verify(mentorshipRequestRepository, Mockito.times(1))
                .save(request);
    }

    @Test
    void testGetRequests_AllFiltersAreUsed_ShouldReturnListOfRequestDtos() {
        RequestFilterDto requestFilterDto = buildRequestFilter();
        MentorshipRequest mentorshipRequest = buildRequest().builder().build();

        Mockito
                .when(mentorshipRequestMapper.toEntity(requestFilterDto, mentorshipRequestService, filterRequestStatusValidator))
                .thenReturn(mentorshipRequest);

        Mockito
                .when(mentorshipRequestRepository.findAll())
                .thenReturn(getListOfRequests(mentorshipRequest.getDescription(), mentorshipRequest.getRequester(), mentorshipRequest.getReceiver()));

        Assertions.assertEquals(1, mentorshipRequestService.getRequests(requestFilterDto).size());
    }

    @Test
    void testGetRequests_NotAllFiltersAreUsed_ShouldReturnListOfRequestDtos() {
        RequestFilterDto requestFilterDto = RequestFilterDto.builder().description("description").build();
        MentorshipRequest mentorshipRequest = MentorshipRequest.builder().description("description").build();

        Mockito
                .when(mentorshipRequestMapper.toEntity(requestFilterDto, mentorshipRequestService, filterRequestStatusValidator))
                .thenReturn(mentorshipRequest);

        Mockito
                .when(mentorshipRequestRepository.findAll())
                .thenReturn(getListOfRequests(requestFilterDto.getDescription()));

        Assertions.assertEquals(1, mentorshipRequestService.getRequests(requestFilterDto).size());
    }

    @Test
    void findUserById() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));

        mentorshipRequestService.findUserById(Mockito.anyLong());

        Mockito.verify(userRepository, Mockito.times(1)).findById(Mockito.anyLong());
    }

    private List<MentorshipRequest> getListOfRequests(String description) {
        MentorshipRequest mentorshipRequest1 = MentorshipRequest.builder().description(description).build();
        MentorshipRequest mentorshipRequest2 = MentorshipRequest.builder().description("descr").build();

        return List.of(
                mentorshipRequest1,
                mentorshipRequest2
        );
    }

    @Test
    void testAcceptRequest_InputsAreValid_ShouldAddMentorAndMentee() {
        MentorshipRequest mentorshipRequest = buildRequest();

        Mockito.when(mentorshipRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.acceptRequest(mentorshipRequest.getId());

        Assertions.assertTrue(mentorshipRequest.getReceiver().getMentees().contains(mentorshipRequest.getRequester()));
        Assertions.assertTrue(mentorshipRequest.getRequester().getMentors().contains(mentorshipRequest.getReceiver()));
    }

    @Test
    void testAcceptRequest_InputsAreValid_ShouldChangeStatusToAcceptedAndSaveRequest() {
        MentorshipRequest mentorshipRequest = buildRequest();

        Mockito.when(mentorshipRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.acceptRequest(mentorshipRequest.getId());

        Assertions.assertEquals(RequestStatus.ACCEPTED, mentorshipRequest.getStatus());
        Mockito.verify(mentorshipRequestRepository, Mockito.times(1)).save(mentorshipRequest);
    }

    @Test
    void testAcceptRequest_InputsAreValid_ShouldSaveMentorAndMentee() {
        MentorshipRequest mentorshipRequest = buildRequest();

        Mockito.when(mentorshipRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.acceptRequest(mentorshipRequest.getId());

        Mockito.verify(userRepository, Mockito.times(1)).save(mentorshipRequest.getReceiver());
        Mockito.verify(userRepository, Mockito.times(1)).save(mentorshipRequest.getRequester());
    }

    @Test
    void testAcceptRequest_InputsAreInvalid_ShouldThrowException() {
        MentorshipRequest request = buildRequest().builder()
                .status(RequestStatus.ACCEPTED)
                .receiver(User.builder()
                        .mentees(new ArrayList<>(List.of(buildRequest().getRequester())))
                        .build())
                .build();

        Mockito.when(mentorshipRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(request));

        Assertions.assertThrows(RequestIsAlreadyAcceptedException.class,
                () -> mentorshipRequestService.acceptRequest(request.getId()));
    }

    @Test
    void testRejectRequest_InputsAreInvalid_ShouldThrowException() {
        long id = 1;
        Mockito.when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(NoRequestsException.class,
                () -> mentorshipRequestService.rejectRequest(id, new RejectionDto("reason")));
    }

    @Test
    void testRejectRequest_InputsAreValidAndRequestWasAccepted_ShouldDeleteMentorAndMenteeFromLists() {
        MentorshipRequest request = buildRequestForRejectingRequest();

        Mockito.when(mentorshipRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        mentorshipRequestService.rejectRequest(request.getId(), new RejectionDto("reason"));

        Assertions.assertFalse(request.getReceiver().getMentees().contains(request.getRequester()));
        Assertions.assertFalse(request.getRequester().getMentors().contains(request.getReceiver()));
    }

    @Test
    void testRejectRequest_InputsAreValidAndRequestWasAccepted_StatusShouldBeRejected() {
        MentorshipRequest request = buildRequestForRejectingRequest();

        Mockito.when(mentorshipRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        mentorshipRequestService.rejectRequest(request.getId(), new RejectionDto("reason"));

        Assertions.assertEquals(RequestStatus.REJECTED, request.getStatus());

    }

    @Test
    void testRejectRequest_InputsAreValidAndRequestWasAccepted_RequestAndUsersShouldBeSaved() {
        MentorshipRequest request = buildRequestForRejectingRequest();

        Mockito.when(mentorshipRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        mentorshipRequestService.rejectRequest(request.getId(), new RejectionDto("reason"));

        Mockito.verify(mentorshipRequestRepository, Mockito.times(1)).save(request);
        Mockito.verify(userRepository, Mockito.times(1)).save(request.getReceiver());
        Mockito.verify(userRepository, Mockito.times(1)).save(request.getRequester());
    }

    @Test
    void testRejectRequest_InputsAreValidAndRequestWasPending_ShouldAddMentorAndMenteeToLists() {
        MentorshipRequest foundRequest = buildRequest();

        Mockito.when(mentorshipRequestRepository.findById(foundRequest.getId())).thenReturn(Optional.of(foundRequest));

        mentorshipRequestService.rejectRequest(foundRequest.getId(), new RejectionDto("reason"));

        Assertions.assertFalse(foundRequest.getReceiver().getMentees().contains(foundRequest.getRequester()));
        Assertions.assertFalse(foundRequest.getRequester().getMentors().contains(foundRequest.getReceiver()));
    }

    @Test
    void testRejectRequest_InputsAreValidAndRequestWasPending_RequestShouldBeRejected() {
        MentorshipRequest foundRequest = buildRequest();

        Mockito.when(mentorshipRequestRepository.findById(foundRequest.getId())).thenReturn(Optional.of(foundRequest));

        mentorshipRequestService.rejectRequest(foundRequest.getId(), new RejectionDto("reason"));

        Assertions.assertEquals(RequestStatus.REJECTED, foundRequest.getStatus());
    }

    @Test
    void testRejectRequest_InputsAreValidAndRequestWasPending_RequestAndUsersShouldBeSaved() {
        MentorshipRequest foundRequest = buildRequest();

        Mockito.when(mentorshipRequestRepository.findById(foundRequest.getId())).thenReturn(Optional.of(foundRequest));

        mentorshipRequestService.rejectRequest(foundRequest.getId(), new RejectionDto("reason"));

        Mockito.verify(mentorshipRequestRepository, Mockito.times(1)).save(foundRequest);
        Mockito.verify(userRepository, Mockito.times(1)).save(foundRequest.getReceiver());
        Mockito.verify(userRepository, Mockito.times(1)).save(foundRequest.getRequester());
    }

    @Test
    void testRejectRequest_RequestIsAlreadyRejected_ShouldThrowException() {
        MentorshipRequest request = buildRequest().builder()
                .status(RequestStatus.REJECTED)
                .build();

        Mockito.when(mentorshipRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        Assertions.assertThrows(RequestIsAlreadyRejectedException.class,
                () -> mentorshipRequestService.rejectRequest(request.getId(), new RejectionDto("reason")));
    }

    private List<MentorshipRequest> getListOfRequests(String description,
                                                      User requester,
                                                      User receiver) {

        MentorshipRequest mentorshipRequest1 = buildRequest().builder()
                .description(description)
                .requester(requester)
                .receiver(receiver)
                .status(RequestStatus.PENDING)
                .build();

        MentorshipRequest mentorshipRequest2 = buildRequest().builder()
                .description("descr")
                .requester(User.builder().id(4).build())
                .receiver(receiver)
                .status(RequestStatus.PENDING)
                .build();

        return List.of(
                mentorshipRequest1,
                mentorshipRequest2
        );
    }

    private MentorshipRequest buildRequest() {
        return MentorshipRequest.builder()
                .id(2)
                .description("description")
                .receiver(User.builder()
                        .id(1)
                        .mentees(new ArrayList<>()).build())
                .requester(User.builder()
                        .id(2)
                        .mentors(new ArrayList<>()).build())
                .status(RequestStatus.PENDING)
                .build();
    }

    private MentorshipRequest buildRequestForRejectingRequest() {
        return buildRequest().builder()
                .status(RequestStatus.ACCEPTED)
                .requester(User.builder()
                        .mentors(new ArrayList<>(List.of(buildRequest().getReceiver())))
                        .build())
                .receiver(User.builder()
                        .mentees(new ArrayList<>(List.of(buildRequest().getRequester())))
                        .build())
                .build();
    }

    private RequestFilterDto buildRequestFilter() {
        return RequestFilterDto.builder()
                .description("description")
                .requesterId(1L)
                .receiverId(2L)
                .status("PENDING")
                .build();
    }

    private MentorshipRequestDto buildRequestDto() {
        return MentorshipRequestDto.builder()
                .description("description")
                .requesterId(1L)
                .receiverId(2L)
                .build();
    }
}
