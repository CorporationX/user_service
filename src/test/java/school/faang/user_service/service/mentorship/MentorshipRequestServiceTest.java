package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import school.faang.user_service.dto.mentorship.MentorshipOfferedEventDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.mentorship_request.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.mapper.MentorshipOfferedEventMapper;
import school.faang.user_service.mapper.mentorship.MentorshipAcceptedRequestMapper;
import school.faang.user_service.mapper.mentorship.MentorshipAcceptedRequestMapperImpl;
import school.faang.user_service.publisher.MentorshipAcceptedEventPublisher;
import school.faang.user_service.publisher.MentorshipOfferedEventPublisher;
import school.faang.user_service.publisher.MentorshipRequestedEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MentorshipRequestServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private List<MentorshipRequestFilter> mentorshipRequestFilters;
    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;
    @Mock
    private MentorshipOfferedEventMapper mentorshipOfferedEventMapper;
    @Mock
    private MentorshipOfferedEventPublisher mentorshipOfferedEventPublisher;
    @Mock
    private MentorshipRequestedEventPublisher mentorshipRequestedEventPublisher;
    @Spy
    private MentorshipAcceptedRequestMapperImpl acceptedRequestMapper;
    @Mock
    private MentorshipAcceptedEventPublisher mentorshipAcceptedEventPublisher;
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
        mentorshipRequestDto = MentorshipRequestDto.builder()
                .id(0L)
                .description(description)
                .requesterId(requesterId)
                .receiverId(receiverId)
                .build();

        mentorshipRequest =
                new MentorshipRequest();

        requester.setId(requesterId);
        requester.setMentors(new ArrayList<>());
        receiver.setId(receiverId);
        receiver.setMentees(new ArrayList<>());
        receiver.setEmail("test@me.com");

        mentorshipRequest.setId(mentorshipRequestId);
        mentorshipRequest.setDescription(description);
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setStatus(RequestStatus.PENDING);
    }

    @Test
    public void testRequestMentorship() {
        MentorshipOfferedEventDto mentorshipOfferedEventDto = MentorshipOfferedEventDto.builder()
                .build();
        Mockito.when(mentorshipRequestMapper.toEntity(mentorshipRequestDto))
                        .thenReturn(mentorshipRequest);
        Mockito.when(mentorshipRequestMapper.toDto(mentorshipRequest))
                .thenReturn(mentorshipRequestDto);
        Mockito.when(mentorshipRequestRepository.save(mentorshipRequest))
                        .thenReturn(mentorshipRequest);
        Mockito.when(mentorshipOfferedEventMapper.toMentorshipOfferedEvent(mentorshipRequestDto))
                .thenReturn(mentorshipOfferedEventDto);

        mentorshipRequestService.requestMentorship(mentorshipRequestDto);

        Mockito.verify(mentorshipRequestValidator, Mockito.times(1)).requestValidate(mentorshipRequestDto);
        Mockito.verify(mentorshipRequestMapper, Mockito.times(1)).toEntity(mentorshipRequestDto);
        Mockito.verify(mentorshipRequestRepository, Mockito.times(1)).save(mentorshipRequestMapper.toEntity(mentorshipRequestDto));
    }

    @Test
    public void testGetRequests() {
        RequestFilterDto filter = new RequestFilterDto(description, requester.getId(), receiver.getId(), RequestStatus.PENDING);
        List<MentorshipRequest> mentorshipRequests = new ArrayList<>();

        Mockito.when(mentorshipRequestRepository.findAll())
                .thenReturn(mentorshipRequests);
        Mockito.when(mentorshipRequestMapper.toDto(mentorshipRequests))
                        .thenReturn(new ArrayList<>());

        mentorshipRequestService.getRequests(filter);
        Mockito.verify(mentorshipRequestRepository, Mockito.times(1)).findAll();
        Mockito.verify(mentorshipRequestFilters, Mockito.times(1)).stream();
        Mockito.verify(mentorshipRequestMapper, Mockito.times(1)).toDto(mentorshipRequests);
    }

    @Test
    public void testAcceptRequest() {
        long id = mentorshipRequest.getId();

        Mockito.when(mentorshipRequestRepository.findById(id))
                        .thenReturn(Optional.of(mentorshipRequest));
        Mockito.when(mentorshipRequestMapper.toDto(mentorshipRequest))
                .thenReturn(mentorshipRequestDto);

        mentorshipRequestService.acceptRequest(id);

        Mockito.verify(mentorshipRequestRepository, Mockito.times(1)).findById(id);
        Mockito.verify(mentorshipRequestValidator, Mockito.times(1)).acceptRequestValidator(requester.getId(),receiver.getId(), RequestStatus.PENDING);
        assertEquals(RequestStatus.ACCEPTED,mentorshipRequest.getStatus());
    }

    @Test
    public void testRejectRequest() {
        long id = mentorshipRequest.getId();
        RejectionDto rejectionDto = new RejectionDto("anyReason");

        Mockito.when(mentorshipRequestRepository.findById(id))
                .thenReturn(Optional.of(mentorshipRequest));
        Mockito.when(mentorshipRequestMapper.toDto(mentorshipRequest))
                .thenReturn(mentorshipRequestDto);

        mentorshipRequestService.rejectRequest(id,rejectionDto);

        Mockito.verify(mentorshipRequestRepository, Mockito.times(1))
                .findById(id);
        Mockito.verify(mentorshipRequestValidator, Mockito.times(1))
                .rejectRequestValidator(RequestStatus.PENDING);

        assertEquals(RequestStatus.REJECTED,mentorshipRequest.getStatus());
        assertEquals(rejectionDto.getReason(),mentorshipRequest.getRejectionReason());
    }

    @Test
    public void testSendNotification(){
        MentorshipOfferedEventDto mentorshipOfferedEventDto = MentorshipOfferedEventDto.builder()
                .build();
        Mockito.when(userService.findUserById(1L))
                .thenReturn(requester);
        Mockito.when(userService.findUserById(2L))
                .thenReturn(receiver);
        Mockito.when(mentorshipRequestMapper.toEntity(mentorshipRequestDto))
                .thenReturn(mentorshipRequest);
        Mockito.when(mentorshipRequestMapper.toDto(mentorshipRequest))
                .thenReturn(mentorshipRequestDto);
        Mockito.when(mentorshipRequestRepository.save(mentorshipRequest))
                .thenReturn(mentorshipRequest);
        Mockito.when(mentorshipOfferedEventMapper.toMentorshipOfferedEvent(mentorshipRequestDto))
                .thenReturn(mentorshipOfferedEventDto);

        mentorshipRequestService.requestMentorship(mentorshipRequestDto);

        Mockito.verify(mentorshipOfferedEventMapper, Mockito.times(1))
                .toMentorshipOfferedEvent(mentorshipRequestDto);
        Mockito.verify(mentorshipOfferedEventPublisher, Mockito.times(1))
                .publish(mentorshipOfferedEventDto);
    }
}
