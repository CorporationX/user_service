package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.util.validator.MentorshipRequestValidator;

import java.util.Optional;

public class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

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
                .create(requesterId, receiverId, description);
    }

    @Test
    void findUserById() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));

        mentorshipRequestService.findUserById(Mockito.anyLong());

        Mockito.verify(userRepository, Mockito.times(1)).findById(Mockito.anyLong());
    }
}

