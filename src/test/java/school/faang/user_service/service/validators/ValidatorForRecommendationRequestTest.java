package school.faang.user_service.service.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.ValidatorForRecommendationRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ValidatorForRecommendationRequestTest {

    @Mock
    private RecommendationRequestRepository requestRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ValidatorForRecommendationRequest validator;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(validator, "MONTH_FOR_SEARCH_REQUEST", 3L);
    }

    @Test
    public void testValidate_RequesterEqualsReceiver_ShouldThrowException() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setRequesterId(1L);
        requestDto.setRecieverId(1L);

        assertThrows(DataValidationException.class, () -> validator.validate(requestDto));
    }

    @Test
    public void testValidate_RequesterDoesNotExist_ShouldThrowException() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setRequesterId(1L);
        requestDto.setRecieverId(2L);

        doThrow(new DataValidationException("User not found")).when(userService).existUserById(1L);

        assertThrows(DataValidationException.class, () -> validator.validate(requestDto));
    }

    @Test
    public void testValidate_ReceiverDoesNotExist_ShouldThrowException() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setRequesterId(1L);
        requestDto.setRecieverId(2L);

        doNothing().when(userService).existUserById(1L);
        doThrow(new DataValidationException("User not found")).when(userService).existUserById(2L);

        assertThrows(DataValidationException.class, () -> validator.validate(requestDto));
    }

    @Test
    public void testValidaRecommendationRequestByIdAndUpdateAt_RequestExistsAndTimeNotPassed_ShouldThrowException() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setId(1L);
        requestDto.setUpdatedAt(LocalDateTime.now().minusMonths(1));

        when(requestRepository.existsById(1L)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> validator.validaRecommendationRequestByIdAndUpdateAt(requestDto));
    }

    @Test
    public void testValidaRecommendationRequestByIdAndUpdateAt_RequestExistsAndTimePassed_ShouldNotThrowException() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setId(1L);
        requestDto.setUpdatedAt(LocalDateTime.now().minusMonths(4));

        when(requestRepository.existsById(1L)).thenReturn(true);

        validator.validaRecommendationRequestByIdAndUpdateAt(requestDto);

        verify(requestRepository, times(1)).existsById(1L);
    }

    @Test
    public void testValidaRecommendationRequestByIdAndUpdateAt_RequestDoesNotExist_ShouldNotThrowException() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setId(1L);

        when(requestRepository.existsById(1L)).thenReturn(false);

        validator.validaRecommendationRequestByIdAndUpdateAt(requestDto);

        verify(requestRepository, times(1)).existsById(1L);
    }
}