package school.faang.user_service.validator.mentorship;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CheckingExistUserValidatorTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    private CheckingExistUserValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CheckingExistUserValidator(mentorshipRepository);
    }

    @Test
    @DisplayName("validate should pass when both users exist")
    void validate_shouldPass_whenBothUsersExist() {
        MentorshipRequestDto dto = new MentorshipRequestDto(1L, 2L, "description");

        Mockito.when(mentorshipRepository.existsById(1L)).thenReturn(true);
        Mockito.when(mentorshipRepository.existsById(2L)).thenReturn(true);

        Assertions.assertDoesNotThrow(() -> validator.validate(dto));

        verify(mentorshipRepository).existsById(1L);
        verify(mentorshipRepository).existsById(2L);
    }

    @Test
    @DisplayName("validate should throw when requester does not exist")
    void validate_shouldThrow_whenRequesterMissing() {
        MentorshipRequestDto dto = new MentorshipRequestDto(1L, 2L, "description");

        Mockito.when(mentorshipRepository.existsById(1L)).thenReturn(false);

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> validator.validate(dto));
        Assertions.assertEquals("RequesterId or receiverId not found", exception.getReason());
        Assertions.assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    @DisplayName("validate should throw when receiver does not exist")
    void validate_shouldThrow_whenReceiverMissing() {
        MentorshipRequestDto dto = new MentorshipRequestDto(1L, 2L, "description");

        Mockito.when(mentorshipRepository.existsById(1L)).thenReturn(true);
        Mockito.when(mentorshipRepository.existsById(2L)).thenReturn(false);

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> validator.validate(dto));
        Assertions.assertEquals("RequesterId or receiverId not found", exception.getReason());
        Assertions.assertEquals(404, exception.getStatusCode().value());
    }
}