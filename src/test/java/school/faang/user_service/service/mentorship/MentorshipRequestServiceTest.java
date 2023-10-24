package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MentorshipMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import java.util.Optional;
import java.util.Random;

import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(value = {MockitoExtension.class})
class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @Mock
    private MentorshipMapper mentorshipMapper;

    @Mock
    private UserRepository userRepository;
    @Mock
    private MentorshipFilter mentorshipFilter;

    private RequestFilterDto requestFilterDto;

    @BeforeEach
    void setUp() {

    }

    @Test
    void testRequestUserEqualReceiver() {
        Long userId = new Random().nextLong();

        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto("Test", userId, userId, RequestStatus.ACCEPTED);

        assertThrows(IllegalArgumentException.class, () -> {
            mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        });
    }

    @Test
    void testRequestUserReceiverUserZero() {
        Long userId = 0L;
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto("Test", userId, userId, RequestStatus.ACCEPTED);

        assertThrows(IllegalArgumentException.class, () -> {
            mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        });
    }

    @Test
    void testRequestMentorshipSuccess() {
        Long requestUserId = 1L;
        Long receiverUserId = 2L;

        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto("Test", requestUserId, receiverUserId, RequestStatus.ACCEPTED);

        when(mentorshipMapper.toEntity(mentorshipRequestDto)).thenReturn(any());
        when(userRepository.findById(mentorshipRequestDto.getRequesterId())).thenReturn(Optional.of(new User()));
        when(userRepository.findById(mentorshipRequestDto.getReceiverId())).thenReturn(Optional.of(new User()));
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        mentorshipMapper.toEntity(mentorshipRequestDto);
        verify(mentorshipMapper, times(2)).toEntity(mentorshipRequestDto);
        verify(mentorshipRequestRepository).save(any());
    }

    @Test
    void requestFilterDtoIsWorked() {
        RequestFilterDto requestFilterDto = new RequestFilterDto("Test", "Test", "Test", RequestStatus.ACCEPTED);
        Assertions.assertDoesNotThrow(() -> mentorshipRequestService.getRequests(requestFilterDto));
    }

    @Test
    void requestFilterDtoIsNotWorked() {
        when(mentorshipRequestService.getRequests(null)).thenThrow(NullPointerException.class);
        Assertions.assertThrows(NullPointerException.class, () -> mentorshipRequestService.getRequests(null));
    }
}