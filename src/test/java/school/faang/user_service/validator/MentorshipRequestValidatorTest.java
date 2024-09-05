package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestValidatorTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MentorshipRequestValidator mentorshipRequestValidator;

    private MentorshipRequestDto mentorshipRequestDto;

    @BeforeEach
    public void setUp() {
        mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setRequesterId(1L);
        mentorshipRequestDto.setReceiverId(2L);
    }

    @Test
    public void testDescriptionValidation_NullDescription_ThrowsException() {
        mentorshipRequestDto.setDescription(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestValidator.descriptionValidation(mentorshipRequestDto));
        assertEquals("Описание обязательно для запроса на менторство", exception.getMessage());
    }

    @Test
    public void testDescriptionValidation_EmptyDescription_ThrowsException() {
        mentorshipRequestDto.setDescription("");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestValidator.descriptionValidation(mentorshipRequestDto));
        assertEquals("Описание обязательно для запроса на менторство", exception.getMessage());
    }

    @Test
    public void testRequesterReceiverValidation_RequesterNotFound_ThrowsException() {
        when(userRepository.existsById(mentorshipRequestDto.getRequesterId())).thenReturn(false);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipRequestValidator.requesterReceiverValidation(mentorshipRequestDto));
        assertEquals("Пользователь, который запрашивает менторство (id1), не найден", exception.getMessage());
    }

    @Test
    public void testRequesterReceiverValidation_ReceiverNotFound_ThrowsException() {
        when(userRepository.existsById(mentorshipRequestDto.getRequesterId())).thenReturn(true);
        when(userRepository.existsById(mentorshipRequestDto.getReceiverId())).thenReturn(false);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipRequestValidator.requesterReceiverValidation(mentorshipRequestDto));
        assertEquals("Пользователь, у которого запрашивают менторство (id2), не найден", exception.getMessage());
    }

    @Test
    public void testLastRequestDateValidation_WithinThreeMonths_ThrowsException() {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(2);
        MentorshipRequest lastRequest = new MentorshipRequest();
        lastRequest.setUpdatedAt(threeMonthsAgo);
        when(mentorshipRequestRepository.findLatestRequest(1L, 2L)).thenReturn(Optional.of(lastRequest));

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipRequestValidator.lastRequestDateValidation(mentorshipRequestDto));
        assertEquals("Пользователь id1 отправлял запрос на менторство менее 3 месяцев назад", exception.getMessage());
    }

    @Test
    public void testLastRequestDateValidation_NoLastRequest_Valid() {
        mentorshipRequestDto.setRequesterId(1L);
        mentorshipRequestDto.setReceiverId(2L);
        when(mentorshipRequestRepository.findLatestRequest(1L, 2L)).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> mentorshipRequestValidator.lastRequestDateValidation(mentorshipRequestDto));
    }

    @Test
    public void testSelfRequestValidation_SelfRequest_ThrowsException() {
        mentorshipRequestDto.setReceiverId(1L);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipRequestValidator.selfRequestValidation(mentorshipRequestDto));
        assertEquals("Вы не можете отправить запрос себе", exception.getMessage());
    }

    @Test
    public void testSelfRequestValidation_Valid() {
        mentorshipRequestDto.setReceiverId(2L);
        assertDoesNotThrow(() -> mentorshipRequestValidator.selfRequestValidation(mentorshipRequestDto));
    }

    @Test
    public void testRequestValidation_RequestNotFound_ThrowsException() {
        Long requestId = 1L;
        when(mentorshipRequestRepository.existsById(requestId)).thenReturn(false);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipRequestValidator.requestValidation(requestId));
        assertEquals("Запрос id1 не найден", exception.getMessage());
    }

    @Test
    public void testRequestValidation_RequestFound_Valid() {
        Long requestId = 1L;
        when(mentorshipRequestRepository.existsById(requestId)).thenReturn(true);
        assertDoesNotThrow(() -> mentorshipRequestValidator.requestValidation(requestId));
    }
}
