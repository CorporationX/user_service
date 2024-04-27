package school.faang.user_service.validation.mentorship;

import school.faang.user_service.handler.exception.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestValidatorTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MentorshipRequestValidator mentorshipRequestValidator;

    @Test
    void requestMentorshipValidationTestReceiverIdEqualsRequesterId() {
        long receiverId = 1;
        long requesterId = 1;
        Assertions.assertThrows(IllegalArgumentException.class, () -> mentorshipRequestValidator.requestMentorshipValidation(requesterId, receiverId));
    }

    @Test
    void requestMentorshipValidationTest() {
        long receiverId = 1;
        long requesterId = 2;

        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder().build()));
        mentorshipRequestValidator.requestMentorshipValidation(requesterId,receiverId);
    }
    @Test
    void requestMentorshipValidationTestEntityNotFound(){
        long receiverId = 1;
        long requesterId = 2;

        Assertions.assertThrows(EntityNotFoundException.class,()->mentorshipRequestValidator
                .requestMentorshipValidation(requesterId,receiverId));
    }

}
