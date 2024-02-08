package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestValidatorTest {
    @InjectMocks
    private MentorshipRequestValidator validator;

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    private MentorshipRequestDto requestDto;
    private MentorshipRequest existingRequest;

    @BeforeEach
    void setUp() {
        requestDto = new MentorshipRequestDto();
        existingRequest = new MentorshipRequest();
    }
    @Test
    void validateRequestTime_IfTooEarly_Throws() {
        existingRequest.setCreatedAt(LocalDateTime.now().minusMonths(2));
        when(mentorshipRequestRepository.findLatestRequest(1L, 2L)).thenReturn(Optional.of(existingRequest));

        DataValidationException exception = assertThrows(DataValidationException.class, () -> validator.validateRequestTime(1L, 2L));
        assertEquals("Запрос можно отправлять раз в три месяца", exception.getMessage());
    }

    @Test
    void validateRequestTime_IfTimeOk_NoThrow() {
        existingRequest.setCreatedAt(LocalDateTime.now().minusMonths(4));
        when(mentorshipRequestRepository.findLatestRequest(1L, 2L)).thenReturn(Optional.of(existingRequest));

        assertDoesNotThrow(() -> validator.validateRequestTime(1L, 2L));
    }

    @Test
    void validateUserIds_IfEquals_Throws() {
        DataValidationException exception = assertThrows(DataValidationException.class, () -> validator.validateUserIds(1L, 1L));

        assertEquals("requesterId и receiverId не могут совпадать", exception.getMessage());
    }

    @Test
    void validateUserIds_IfDifferent_NoThrow() {
        assertDoesNotThrow(() -> validator.validateUserIds(1L, 2L));
    }

    @Test
    void validateDescription_IfEmpty_Throws() {
        requestDto.setDescription("");

        DataValidationException exception = assertThrows(DataValidationException.class, () -> validator.validateDescription(requestDto));
        assertEquals("Нет описания", exception.getMessage());
    }

    @Test
    void validateDescription_IfNull_Throws() {
        requestDto.setDescription(null);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> validator.validateDescription(requestDto));
        assertEquals("Нет описания", exception.getMessage());
    }

    @Test
    void validateDescription_IfNotEmpty_NoThrow() {
        requestDto.setDescription("Description");

        assertDoesNotThrow(() -> validator.validateDescription(requestDto));
    }
}
