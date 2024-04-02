package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.recommendation.RecommendationRequestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestControllerTest {

    @Mock
    private RecommendationRequestService recommendationRequestService;

    @InjectMocks
    private RecommendationRequestController recommendationRequestController;

    @Test
    public void testRequestRecommendation_WithValidInput_ReturnsDto() {
        RecommendationRequestDto inputDto = new RecommendationRequestDto();
        inputDto.setMessage("Test message");
        inputDto.setRequesterId(1L);
        inputDto.setReceiverId(2L);
        inputDto.setSkills(Collections.singletonList("Java"));

        // Имитация поведения метода recommendationRequestService.create()
        doAnswer(invocation -> {
            // Первый аргумент имеет индекс 0, поэтому мы получаем второй аргумент (индекс 1) - это recommendationRequestDto.
            RecommendationRequestDto recommendationRequestDto = invocation.getArgument(2);
            return recommendationRequestDto; // Возвращаем recommendationRequestDto
        }).when(recommendationRequestService).create(anyLong(), anyLong(), any());

        // Действие
        RecommendationRequestDto result = recommendationRequestController.requestRecommendation(inputDto);

        // Проверка
        assertEquals(inputDto, result);
        verify(recommendationRequestService, times(1)).create(anyLong(), anyLong(), any());
    }

    @Test
    public void testRequestRecommendation_WithNullMessage_ThrowsIllegalArgumentException() {
        RecommendationRequestDto inputDto = new RecommendationRequestDto();
        inputDto.setMessage(null); // устанавливаем сообщение как null

        // Действие и Проверка
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestController.requestRecommendation(inputDto);
        });
        assertEquals("Сообщение не может быть пустым", exception.getMessage());
        verify(recommendationRequestService, never()).create(anyLong(), anyLong(), any());
    }
}