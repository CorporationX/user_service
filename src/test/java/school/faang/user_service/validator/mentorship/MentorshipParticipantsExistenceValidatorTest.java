package school.faang.user_service.validator.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.repository.UserRepository;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipParticipantsExistenceValidatorTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private MentorshipParticipantsExistenceValidator validator;

    @Test
    void validate_ThrowsException_WhenReceiverDoesNotExist() {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setReceiverId(2L);
        dto.setRequesterId(1L);

        when(repository.existsById(dto.getReceiverId())).thenReturn(false);
        when(repository.existsById(dto.getRequesterId())).thenReturn(true);

        assertThrows(NoSuchElementException.class, () -> validator.validate(dto), ExceptionMessages.RECEIVER_NOT_FOUND);
    }

    @Test
    void validate_ThrowsException_WhenRequesterDoesNotExist() {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setReceiverId(2L);
        dto.setRequesterId(1L);

        when(repository.existsById(dto.getReceiverId())).thenReturn(true);
        when(repository.existsById(dto.getRequesterId())).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> validator.validate(dto), ExceptionMessages.REQUESTER_NOT_FOUND);
    }

    @Test
    void validate_DoesNothing_WhenBothParticipantsExist() {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setReceiverId(2L);
        dto.setRequesterId(1L);

        when(repository.existsById(dto.getReceiverId())).thenReturn(true);
        when(repository.existsById(dto.getRequesterId())).thenReturn(true);

        assertDoesNotThrow(() -> validator.validate(dto));
    }

}