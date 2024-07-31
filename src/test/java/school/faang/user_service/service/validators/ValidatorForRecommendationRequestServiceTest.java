package school.faang.user_service.service.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.exeptions.DataValidationException;
import school.faang.user_service.exeptions.NotFoundElement;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.validator.ValidatorForRecommendationRequestService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ValidatorForRecommendationRequestServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecommendationRequestRepository requestRepository;

    @InjectMocks
    private ValidatorForRecommendationRequestService validator;

    private RecommendationRequestDto validRequestDto;
    private RecommendationRequestDto invalidRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        validRequestDto = new RecommendationRequestDto();
        validRequestDto.setRequesterId(1L);
        validRequestDto.setRecieverId(2L);
        validRequestDto.setId(1L);
        validRequestDto.setUpdatedAt(LocalDateTime.now().minusMonths(7));

        invalidRequestDto = new RecommendationRequestDto();
        invalidRequestDto.setRequesterId(1L);
        invalidRequestDto.setRecieverId(1L);
        invalidRequestDto.setId(1L);
        invalidRequestDto.setUpdatedAt(LocalDateTime.now().minusMonths(5));
    }

    @Test
    void shouldThrowExceptionWhenRequesterIsReciever() {
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setRequesterId(1L);
        dto.setRecieverId(1L);

        assertThrows(DataValidationException.class, () -> validator.validatorData(dto));
    }

    @Test
    void shouldThrowExceptionWhenRequesterDoesNotExist() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundElement.class, () -> validator.validatorData(validRequestDto));
    }

    @Test
    void shouldThrowExceptionWhenRecieverDoesNotExist() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(false);

        assertThrows(NotFoundElement.class, () -> validator.validatorData(validRequestDto));
    }

    @Test
    void shouldThrowExceptionWhenRequestExistsAndLessThan6Months() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(requestRepository.existsById(1L)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> validator.validatorData(invalidRequestDto));
    }

    @Test
    void shouldPassValidationWhenAllConditionsAreMet() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(requestRepository.existsById(1L)).thenReturn(false);

        validator.validatorData(validRequestDto);

        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).existsById(2L);
        verify(requestRepository, times(1)).existsById(1L);
    }
}