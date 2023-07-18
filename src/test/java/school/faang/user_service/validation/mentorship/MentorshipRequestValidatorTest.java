package school.faang.user_service.validation.mentorship;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import school.faang.user_service.entity.User;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validation.MentorshipRequestValidator;

import java.util.Optional;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestValidatorTest {

    @Mock
    private UserService userService;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @InjectMocks
    private MentorshipRequestValidator validator;

    private User receiver;
    private User requester;
    private LocalDateTime threeMonthsAgo;
    private MentorshipRequest freshRequest;
    private MentorshipRequest mentorshipRequest;

    @BeforeEach
    public void setUp() {
        freshRequest = new MentorshipRequest();
        mentorshipRequest = new MentorshipRequest();

        threeMonthsAgo = LocalDateTime.now().minusMonths(3);

        receiver = User.builder().id(1L).username("Vlad").build();
        requester = User.builder().id(2L).username("Pablo").build();
    }

    @Test
    @DisplayName("Test: Positive scenario")
    public void testValidatorPositiveScenario() {
        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setRequester(requester);

        when(userService.findUserById(receiver.getId())).thenReturn(true);
        when(userService.findUserById(requester.getId())).thenReturn(true);
        assertDoesNotThrow(() -> validator.validate(mentorshipRequest));
    }

    @Test
    @DisplayName("Test: Mentor and mentee are the same person")
    public void testValidatorThrowsExceptionWhenRequestingToSelf() {
        mentorshipRequest.setReceiver(requester);
        mentorshipRequest.setRequester(requester);

        assertThrows(IllegalArgumentException.class, () -> validator.validate(mentorshipRequest));
    }

    @Test
    @DisplayName("Test: Mentor is not found")
    public void testValidatorThrowsExceptionWhenMentorNotFound() {
        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setRequester(requester);

        assertThrows(IllegalArgumentException.class, () -> validator.validate(mentorshipRequest));
    }

    @Test
    @DisplayName("Test: Mentee is not found")
    public void testValidatorThrowsExceptionWhenMenteeNotFound() {
        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setRequester(requester);

        when(userService.findUserById(receiver.getId())).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(mentorshipRequest));
        verify(userService).findUserById(receiver.getId());
    }

    @Test
    @DisplayName("Test: Few mentorship requests in less than 3 months")
    public void testValidatorThrowsExceptionWhenRecentRequestExists() {
        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setCreatedAt(threeMonthsAgo.plusDays(1));

        freshRequest.setReceiver(receiver);
        freshRequest.setRequester(requester);
        freshRequest.setCreatedAt(LocalDateTime.now());

        when(userService.findUserById(receiver.getId())).thenReturn(true);
        when(userService.findUserById(requester.getId())).thenReturn(true);
        when(mentorshipRequestRepository.findLatestRequest(requester.getId(), receiver.getId()))
                .thenReturn(Optional.of(mentorshipRequest));

        assertThrows(IllegalArgumentException.class, () -> validator.validate(freshRequest));
    }
}