package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestValidatorTest {
    @InjectMocks
    private RecommendationRequestValidator recommendationRequestValidator;

    @Mock
    private UserRepository userRepository;

    RecommendationRequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = RecommendationRequestDto.builder()
                .id(1L)
                .message("message")
                .status(RequestStatus.ACCEPTED)
                .skillsId(List.of(1L))
                .requesterId(1L)
                .receiverId(1L)
                .createdAt(LocalDateTime.now().minusMonths(7))
                .build();
    }

    @Test
    void testValidationExistByIdThrowDataValidationException() {
        assertThrows(DataValidationException.class, () -> recommendationRequestValidator.validationExistById(1L));
    }

    @Test
    void testValidationExistByIdDoesNotThrowDataValidationException() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> recommendationRequestValidator.validationExistById(1L));
    }

    @Test
    void testValidationRequestDateNegative() {
        requestDto.setCreatedAt(LocalDateTime.now().minusMonths(5));
        assertThrows(DataValidationException.class, () -> recommendationRequestValidator.validationRequestDate(requestDto));
    }

    @Test
    void testValidationRequestDatePositive() {
        assertDoesNotThrow(() -> recommendationRequestValidator.validationRequestDate(requestDto));
    }
}