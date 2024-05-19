package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceImplTest {
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private MentorshipRequestServiceImpl mentorshipRequestService;

    @Test
    public void test1() {
        Long userId = 1L;
        Long mentorId = 2L;
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(userRepository.existsById(mentorId)).thenReturn(true);
        Assertions.assertTrue(mentorshipRequestService.requestMentorship(userId, mentorId, "Description"));
    }
    @Test
    public void test2() {
        Long userId = 1L;
        Long mentorId = 1L;
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(userRepository.existsById(mentorId)).thenReturn(true);
        Mockito.when(mentorshipRequestRepository.findLatestRequest(userId, mentorId)).thenReturn(Optional.empty());
        Assertions.assertFalse(mentorshipRequestService.requestMentorship(userId, mentorId, "Description"));
    }
    @Test
    public void test3() {
        Long userId = 1L;
        Long mentorId = 1L;
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(userRepository.existsById(mentorId)).thenReturn(true);
        Mockito.when(mentorshipRequestRepository.findLatestRequest(userId, mentorId))
                .thenReturn(Optional.of(new MentorshipRequest(0, null, null, null, null,null, LocalDateTime.now().minusMonths(2),null)));
        Assertions.assertFalse(mentorshipRequestService.requestMentorship(userId, mentorId, "Description"));
    }

}