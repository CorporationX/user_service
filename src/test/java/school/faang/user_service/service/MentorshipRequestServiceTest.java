package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.util.validator.FilterRequestStatusValidator;
import school.faang.user_service.util.validator.MentorshipRequestValidator;

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
}

