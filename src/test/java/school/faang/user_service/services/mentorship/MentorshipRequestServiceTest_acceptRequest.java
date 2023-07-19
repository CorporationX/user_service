package school.faang.user_service.services.mentorship;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.volidate.mentorship.AcceptRequestValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MentorshipRequestServiceTest_acceptRequest {

    @Mock
    private AcceptRequestValidator acceptRequestValidator;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;
    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAcceptRequest_InputsAreValid_ShouldComplete() throws Exception {
        long id = 2;

        User receiver = new User();
        User requester = new User();

        receiver.setId(1);
        requester.setId(2);
        receiver.setMentees(new ArrayList<>());
        requester.setMentors(new ArrayList<>());

        MentorshipRequest mentorshipRequest =
                new MentorshipRequest();
        mentorshipRequest.setId(id);
        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setStatus(RequestStatus.PENDING);

        Mockito.when(mentorshipRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.acceptRequest(id);

        Assertions.assertTrue(receiver.getMentees().contains(requester));
        Assertions.assertTrue(requester.getMentors().contains(receiver));
        Assertions.assertEquals(RequestStatus.ACCEPTED, mentorshipRequest.getStatus());
        Mockito.verify(mentorshipRequestRepository, Mockito.times(1)).save(mentorshipRequest);
        Mockito.verify(userRepository, Mockito.times(1)).save(receiver);
        Mockito.verify(userRepository, Mockito.times(1)).save(requester);
    }

    @Test
    void testRequestMentorshipMore3Request() {
        User userRequest = new User();
        User userReceiver = new User();
        List<MentorshipRequest> mentorshipRequests = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            mentorshipRequests.add(new MentorshipRequest());
            mentorshipRequests.get(i).setCreatedAt(LocalDateTime.now());
        }

        userRequest.setSentMentorshipRequests(mentorshipRequests);
        assertThrows(Exception.class,
                () -> mentorshipRequestService.requestMentorship(new MentorshipRequestDto(0, "anyString", userRequest.getId(), userReceiver.getId())));
    }

    // Не проходят тесты с monk, т.к. создаются юзеры, которых нет в бд
    // посмотрел решение других, используют класс optional, но не понял зачем он нужен
    // на данном примере, если нет юзера с таким id, то дальше нет никакого смысла продолжать со значением null
    @Test
    void testRequestMentorshipMore3RequestTrue() throws Exception {
        User userRequest = new User();
        User userReceiver = new User();
        List<MentorshipRequest> mentorshipRequests = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            mentorshipRequests.add(new MentorshipRequest());
            mentorshipRequests.get(i).setCreatedAt(LocalDateTime.now());
        }
        mentorshipRequests.get(0).setCreatedAt(LocalDateTime.now().minusMonths(2));
        userRequest.setSentMentorshipRequests(mentorshipRequests);

        MentorshipRequestDto dto = new MentorshipRequestDto(0, "any text", userRequest.getId(), userReceiver.getId());

        mentorshipRequestService.requestMentorship(dto);
        Mockito.verify(mentorshipRequestRepository, Mockito.times(1)).save(mentorshipRequestMapper.toEntity(dto));
    }

    @Test
    public void testCreateRequestOnMentorship() throws Exception {
        User userRequest = new User();
        User userReceiver = new User();
        MentorshipRequestDto dto = new MentorshipRequestDto(0, "any text", userRequest.getId(), userReceiver.getId());

        mentorshipRequestService.requestMentorship(dto);
        Mockito.verify(mentorshipRequestRepository, Mockito.times(1)).save(mentorshipRequestMapper.toEntity(dto));
    }
}
