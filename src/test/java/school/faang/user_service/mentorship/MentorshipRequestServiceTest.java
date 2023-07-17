package school.faang.user_service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.mentorship.dto.MentorshipRequestDto;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRequestMentorshipOneUser() {
        User oneUser = new User();
        assertThrows(Exception.class,
                () -> mentorshipRequestService.requestMentorship(new MentorshipRequestDto(0, "anyString", oneUser, oneUser)));
    }

    @Test
    void testRequestMentorshipMore3Request() {
        User userRequest = new User();
        User userReceiver = new User();
        List<MentorshipRequest> mentorshipRequests = new ArrayList<>();

        for (int i =0;i<3;i++){
            mentorshipRequests.add(new MentorshipRequest());
            mentorshipRequests.get(i).setCreatedAt(LocalDateTime.now());
        }

        userRequest.setSentMentorshipRequests(mentorshipRequests);
        assertThrows(Exception.class,
                () -> mentorshipRequestService.requestMentorship(new MentorshipRequestDto(0, "anyString", userRequest, userReceiver)));
    }

    @Test
    void testRequestMentorshipMore3RequestTrue() throws Exception {
        User userRequest = new User();
        User userReceiver = new User();
        List<MentorshipRequest> mentorshipRequests = new ArrayList<>();
        for (int i =0;i<3;i++){
            mentorshipRequests.add(new MentorshipRequest());
            mentorshipRequests.get(i).setCreatedAt(LocalDateTime.now());
        }
        mentorshipRequests.get(0).setCreatedAt(LocalDateTime.now().minusMonths(2));
        userRequest.setSentMentorshipRequests(mentorshipRequests);

        MentorshipRequestDto dto = new MentorshipRequestDto(0, "any text", userRequest, userReceiver);

        mentorshipRequestService.requestMentorship(dto);
        Mockito.verify(mentorshipRequestRepository, Mockito.times(1)).create(dto.getRequester().getId(), dto.getReceiver().getId(), dto.getDescription());

    }

    @Test
    public void testCreateRequestOnMentorship() throws Exception {
        User userRequest = new User();
        User userReceiver = new User();
        MentorshipRequestDto dto = new MentorshipRequestDto(0, "any text", userRequest, userReceiver);

        mentorshipRequestService.requestMentorship(dto);
        Mockito.verify(mentorshipRequestRepository, Mockito.times(1)).create(dto.getRequester().getId(), dto.getReceiver().getId(), dto.getDescription());
    }

    @Test
    void getRequests() {
    }

    @Test
    void acceptRequest() {
    }

    @Test
    void rejectRequest() {
    }
}