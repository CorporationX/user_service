package school.faang.user_service.validator;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.dto.MentorshipRequestDto;
import school.faang.user_service.model.entity.MentorshipRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

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
    public void descriptionValidationTest_NullDescription() {
        mentorshipRequestDto.setDescription(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestValidator.descriptionValidation(mentorshipRequestDto));
        assertEquals("Описание обязательно для запроса на менторство", exception.getMessage());
    }

    @Test
    public void descriptionValidationTest_EmptyDescription() {
        mentorshipRequestDto.setDescription("");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestValidator.descriptionValidation(mentorshipRequestDto));
        assertEquals("Описание обязательно для запроса на менторство", exception.getMessage());
    }

    @Test
    public void requesterReceiverValidationTest_RequesterNotFound() {
        when(userRepository.existsById(mentorshipRequestDto.getRequesterId())).thenReturn(false);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipRequestValidator.requesterReceiverValidation(mentorshipRequestDto));
        assertEquals("Пользователь, который запрашивает менторство (id1), не найден", exception.getMessage());
    }

    @Test
    public void requesterReceiverValidationTest_ReceiverNotFound() {
        when(userRepository.existsById(mentorshipRequestDto.getRequesterId())).thenReturn(true);
        when(userRepository.existsById(mentorshipRequestDto.getReceiverId())).thenReturn(false);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipRequestValidator.requesterReceiverValidation(mentorshipRequestDto));
        assertEquals("Пользователь, у которого запрашивают менторство (id2), не найден", exception.getMessage());
    }

    @Test
    public void lastRequestDateValidationTest_LessThanThreeMonths() {
        LocalDateTime requestDate = LocalDateTime.now().minusMonths(2);
        MentorshipRequest lastRequest = new MentorshipRequest();
        lastRequest.setUpdatedAt(requestDate);
        when(mentorshipRequestRepository.findLatestRequest(1L, 2L)).thenReturn(Optional.of(lastRequest));

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipRequestValidator.lastRequestDateValidation(mentorshipRequestDto));
        assertEquals("Пользователь id1 отправлял запрос на менторство менее 3 месяцев назад", exception.getMessage());
    }

    @Test
    public void lastRequestDateValidationTest_NoLastRequest() {
        mentorshipRequestDto.setRequesterId(1L);
        mentorshipRequestDto.setReceiverId(2L);
        when(mentorshipRequestRepository.findLatestRequest(1L, 2L)).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> mentorshipRequestValidator.lastRequestDateValidation(mentorshipRequestDto));
    }

    @Test
    public void selfRequestValidationTest_SelfRequest() {
        mentorshipRequestDto.setReceiverId(1L);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipRequestValidator.selfRequestValidation(mentorshipRequestDto));
        assertEquals("Вы не можете отправить запрос себе", exception.getMessage());
    }

    @Test
    public void selfRequestValidationTest_Valid() {
        mentorshipRequestDto.setReceiverId(2L);
        assertDoesNotThrow(() -> mentorshipRequestValidator.selfRequestValidation(mentorshipRequestDto));
    }

    @Test
    public void requestValidationTest_RequestNotFound() {
        Long requestId = 1L;
        when(mentorshipRequestRepository.existsById(requestId)).thenReturn(false);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipRequestValidator.requestValidation(requestId));
        assertEquals("Запрос id1 не найден", exception.getMessage());
    }

    @Test
    public void requestValidationTest_Valid() {
        Long requestId = 1L;
        when(mentorshipRequestRepository.existsById(requestId)).thenReturn(true);
        assertDoesNotThrow(() -> mentorshipRequestValidator.requestValidation(requestId));
    }
}
