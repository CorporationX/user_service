package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshipRequest.RejectionDto;
import school.faang.user_service.dto.mentorshipRequest.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityStateException;
import school.faang.user_service.exception.notFoundExceptions.MentorshipRequestNotFoundException;
import school.faang.user_service.filter.mentorshiprequest.*;
import school.faang.user_service.mapper.MentorshipRequestMapperImpl;
import school.faang.user_service.messaging.MentorshipAcceptedEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.util.validator.MentorshipRequestValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;

    @Spy
    private MentorshipRequestMapperImpl mentorshipRequestMapper;

    @Mock
    private MentorshipAcceptedEventPublisher mentorshipAcceptedEventPublisher;


    private List<MentorshipRequestFilter> filters = List.of(
            new MentorshipRequestReqIdFilter(),
            new MentorshipRequestRecIdFilter(),
            new MentorshipRequestStatusFilter(),
            new MentorshipRequestDescrFilter()
    );

    private MentorshipRequestService service;

    @BeforeEach
    void setUp() {
        service = new MentorshipRequestService(
                mentorshipRequestRepository,
                mentorshipRequestMapper,
                mentorshipRequestValidator,
                userRepository,
                mentorshipAcceptedEventPublisher,
                filters
        );
    }

    @Test
    void requestMentorship_ShouldMapCorrectly() {
        MentorshipRequestDto requestDto = buildRequestDto();

        MentorshipRequest actual = mentorshipRequestMapper.toEntity(requestDto);

        Assertions.assertEquals(buildRequest(), actual);
    }

    @Test
    void requestMentorship_ValidationCompleted_ShouldSave() {
        Mockito.doNothing().when(mentorshipRequestValidator).validate(Mockito.any());

        service.requestMentorship(buildRequestDto());

        Mockito.verify(mentorshipRequestRepository)
                .save(Mockito.any());
    }

    @Test
    void getRequests_AllFiltersAreUsed_ShouldReturnListOfRequestDtos() {
        Mockito.when(mentorshipRequestRepository.findAll())
                .thenReturn(getStreamOfRequests().toList());
        List<MentorshipRequestDto> expected = List.of(
                buildRequestDto()
        );

        List<MentorshipRequestDto> actual = service.getRequests(buildRequestFilter());

        Assertions.assertIterableEquals(expected, actual);
    }

    @Test
    void testAcceptRequest_RequestNoFound_ShouldThrowException() {
        Mockito.when(mentorshipRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(MentorshipRequestNotFoundException.class,
                () -> service.acceptRequest(1L));
    }

    @Test
    void testAcceptRequest_AlreadyAccepted_ShouldThrowException() {
        Mockito.when(mentorshipRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(
                        MentorshipRequest.builder()
                                .receiver(User.builder()
                                        .id(1L)
                                        .mentees(List.of(User.builder().id(3L).build()))
                                        .build())
                                .requester(User.builder().id(2L).build())
                                .status(RequestStatus.ACCEPTED)
                                .build()
                ));

        Assertions.assertThrows(EntityStateException.class,
                () -> service.acceptRequest(1L));
    }

    @Test
    void testAcceptRequest_MentorAlreadyHasThisMentee_ShouldThrowException() {
        User requester = User.builder().id(2L).build();
        Mockito.when(mentorshipRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(
                        MentorshipRequest.builder()
                                .receiver(User.builder()
                                        .id(1L)
                                        .mentees(List.of(requester))
                                        .build())
                                .requester(requester)
                                .status(RequestStatus.PENDING)
                                .build()
                ));

        Assertions.assertThrows(EntityStateException.class,
                () -> service.acceptRequest(1L));
    }

    @Test
    void testAcceptRequest_InputsAreValid_ShouldAddMentorAndMentee() {
        MentorshipRequest request = buildRequestForAcceptingRequest();

        Mockito.when(mentorshipRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(request));

        service.acceptRequest(1L);

        Assertions.assertTrue(request.getReceiver().getMentees().contains(request.getRequester()));
        Assertions.assertTrue(request.getRequester().getMentors().contains(request.getReceiver()));
    }

    @Test
    void testAcceptRequest_InputsAreValid_ShouldChangeStatusToAcceptedAndSaveRequest() {
        MentorshipRequest request = buildRequestForAcceptingRequest();

        Mockito.when(mentorshipRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(request));

        service.acceptRequest(1L);

        Assertions.assertEquals(RequestStatus.ACCEPTED, request.getStatus());
        Mockito.verify(mentorshipRequestRepository).save(request);
    }

    @Test
    void testAcceptRequest_InputsAreValid_ShouldPublishToTopic() {
        MentorshipRequest request = buildRequestForAcceptingRequest();

        Mockito.when(mentorshipRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(request));

        service.acceptRequest(1L);

        Mockito.verify(mentorshipAcceptedEventPublisher).publish(Mockito.any());
    }

    @Test
    void testAcceptRequest_InputsAreValid_ShouldSaveMentorAndMentee() {
        MentorshipRequest request = buildRequestForAcceptingRequest();

        Mockito.when(mentorshipRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(request));

        service.acceptRequest(1L);

        Mockito.verify(userRepository).save(request.getReceiver());
        Mockito.verify(userRepository).save(request.getRequester());
    }

    @Test
    void testRejectRequest_RequestNoFound_ShouldThrowException() {
        Mockito.when(mentorshipRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(MentorshipRequestNotFoundException.class,
                () -> service.rejectRequest(1L, new RejectionDto("reason")));
    }

    @Test
    void testRejectRequest_AlreadyRejected_ShouldThrowException() {
        Mockito.when(mentorshipRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(
                        MentorshipRequest.builder()
                                .receiver(User.builder()
                                        .id(1L)
                                        .mentees(List.of(User.builder().id(3L).build()))
                                        .build())
                                .requester(User.builder().id(2L).build())
                                .status(RequestStatus.REJECTED)
                                .build()
                ));

        Assertions.assertThrows(EntityStateException.class,
                () -> service.rejectRequest(1L, new RejectionDto("reason")));
    }

    @Test
    void testRejectRequest_InputsAreValid_ShouldRemoveMentorAndMentee() {
        MentorshipRequest request = buildRequestForRejectingRequest();

        Mockito.when(mentorshipRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(request));

        service.rejectRequest(1L, new RejectionDto("reason"));

        Assertions.assertFalse(request.getReceiver().getMentees().contains(request.getRequester()));
        Assertions.assertFalse(request.getRequester().getMentors().contains(request.getReceiver()));
    }

    @Test
    void testRejectRequest_InputsAreValid_ShouldChangeStatusToRejectedAndSaveRequest() {
        MentorshipRequest request = buildRequestForRejectingRequest();

        Mockito.when(mentorshipRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(request));

        service.rejectRequest(1L, new RejectionDto("reason"));

        Assertions.assertEquals(RequestStatus.REJECTED, request.getStatus());
        Mockito.verify(mentorshipRequestRepository).save(request);
    }

    @Test
    void testRejectRequest_InputsAreValid_ShouldSaveMentorAndMentee() {
        MentorshipRequest request = buildRequestForRejectingRequest();

        Mockito.when(mentorshipRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(request));

        service.rejectRequest(1L, new RejectionDto("reason"));

        Mockito.verify(userRepository).save(request.getReceiver());
        Mockito.verify(userRepository).save(request.getRequester());
    }

    private Stream<MentorshipRequest> getStreamOfRequests() {
        return Stream.<MentorshipRequest>builder()
                .add(buildRequest())
                .add(buildRequest().builder()
                        .description("descr")
                        .requester(User.builder().id(4).build())
                        .status(RequestStatus.PENDING)
                        .build())
                .build();
    }

    private RequestFilterDto buildRequestFilterDto() {
        return RequestFilterDto.builder()
                .description("description")
                .requesterId(1L)
                .receiverId(2L)
                .status(RequestStatus.PENDING)
                .build();
    }

    private MentorshipRequest buildRequest() {
        return MentorshipRequest.builder()
                .description("description")
                .receiver(User.builder()
                        .id(1)
                        .mentees(null).build())
                .requester(User.builder()
                        .id(2)
                        .mentors(null).build())
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

    private MentorshipRequest buildRequestForAcceptingRequest() {
        return MentorshipRequest.builder()
                .id(1L)
                .receiver(User.builder()
                        .id(1L)
                        .mentees(new ArrayList<>())
                        .build())
                .requester(User.builder()
                        .id(2L)
                        .mentors(new ArrayList<>())
                        .build())
                .status(RequestStatus.PENDING)
                .build();
    }

    private RequestFilterDto buildRequestFilter() {
        return RequestFilterDto.builder()
                .description("description")
                .receiverId(1L)
                .requesterId(2L)
                .status(RequestStatus.PENDING)
                .build();
    }

    private MentorshipRequestDto buildRequestDto() {
        return MentorshipRequestDto.builder()
                .description("description")
                .receiverId(1L)
                .requesterId(2L)
                .build();
    }
}
