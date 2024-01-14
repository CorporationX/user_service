package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.mentorship.MentorshipRequestException;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @InjectMocks
    private MentorshipRequestDto mentorshipRequestDto;

    @Mock
    private MentorshipRequest mentorshipRequest;

    @Mock
    private UserRepository userRepository;

    private User requester;

    private User receiver;

    @BeforeEach
    public void init() {
        mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setRequester(1L);
        mentorshipRequestDto.setDescription("Description");
        mentorshipRequestDto.setRequester(88L);
        mentorshipRequestDto.setReceiver(77L);
        mentorshipRequestDto.setCreatedAt(LocalDateTime.now());
        receiver = new User();
        receiver.setId(2L);
        receiver.setUsername("Ivan");
        requester = new User();
        requester.setId(1L);
        requester.setUsername("John");
    }

    @Test
    public void whenRequestForMembershipThenNoDataInDB() {
        try {
            mentorshipRequestService.acceptRequest(1L);
        } catch (MentorshipRequestException e) {
            assertThat(e).isInstanceOf(RuntimeException.class)
                    .hasMessage("There is no mentorship request with this id");
        }
    }

    @Test
    public void whenRequestForMembershipThenAlreadyMentor() {
        Mockito.when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));
        Mockito.when(mentorshipRequest.getRequester()).thenReturn(requester);
        Mockito.when(mentorshipRequest.getReceiver()).thenReturn(receiver);
        requester.setMentors(List.of(receiver));
        try {
            mentorshipRequestService.acceptRequest(1L);
        } catch (MentorshipRequestException e) {
            assertThat(e).isInstanceOf(RuntimeException.class)
                    .hasMessage("Already a mentor");
        }
    }

    @Test
    public void whenRequestForMembershipThenSuccess() {
        Mockito.when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));
        Mockito.when(mentorshipRequest.getRequester()).thenReturn(requester);
        Mockito.when(mentorshipRequest.getReceiver()).thenReturn(receiver);
        requester.setMentors(new ArrayList<>());
        mentorshipRequestService.acceptRequest(1L);
        Mockito.verify(mentorshipRequestRepository, times(1))
                .save(mentorshipRequest);
        Mockito.verify(mentorshipRequestMapper, times(1))
                .toDTO(mentorshipRequest);
    }
}