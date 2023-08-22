package school.faang.user_service.util.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.SameEntityException;
import school.faang.user_service.exception.TimingException;
import school.faang.user_service.exception.notFoundExceptions.MentorshipRequestNotFoundException;
import school.faang.user_service.exception.notFoundExceptions.contact.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class MentorshipRequestValidatorTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MentorshipRequestValidator validator;

    @BeforeEach
    void setUp() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.empty());
        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(User.builder().id(2L).build()));
        Mockito.when(mentorshipRequestRepository.findLatestRequest(1L, 2L))
                .thenReturn(Optional.of(MentorshipRequest.builder().id(1L).createdAt(LocalDateTime.now().minusMonths(2)).build()));
    }

    @Test
    void validate_RequesterIsEmpty_ShouldThrowException() {
        UserNotFoundException e = Assertions.assertThrows(UserNotFoundException.class,
                () -> validator.validate(buildDto()));
        Assertions.assertEquals("User not found", e.getMessage());
    }

    @Test
    void validate_RequesterAndReceiverAreSame_ShouldThrowException() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(User.builder().id(2L).build()));

        SameEntityException e = Assertions.assertThrows(SameEntityException.class,
                () -> validator.validate(buildDto()));
        Assertions.assertEquals("Same mentor and mentee", e.getMessage());
    }

    @Test
    void validate_LastRequestIsMoreThan3Months_ShouldThrowException() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(User.builder().id(1L).build()));

        TimingException e = Assertions.assertThrows(TimingException.class,
                () -> validator.validate(buildDto()));
        Assertions.assertEquals("The request can be sent once every three months", e.getMessage());
    }

    @Test
    void validate_InputsAreCorrect_ShouldComplete() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(User.builder().id(1L).build()));
        Mockito.when(mentorshipRequestRepository.findLatestRequest(1L, 2L))
                .thenReturn(Optional.of(MentorshipRequest.builder().id(1L).createdAt(LocalDateTime.now().minusMonths(4)).build()));

        Assertions.assertDoesNotThrow(() -> validator.validate(buildDto()));
    }

    private MentorshipRequestDto buildDto() {
        return MentorshipRequestDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .description("test")
                .build();
    }
}
