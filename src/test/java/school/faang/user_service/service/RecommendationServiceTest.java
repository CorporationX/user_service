package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import static org.junit.jupiter.api.Assertions.*;

class RecommendationServiceTest {
    @Mock
    private RecommendationRepository recommendationRepository; // Мокируем репозиторий

    @InjectMocks
    private RecommendationService recommendationService; // Внедряем зависимость в сервис

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Инициализация моков
    }

    @Test
    void testCreateRecommendation() {
        // Arrange: подготавливаем данные и настраиваем мок
        RecommendationDto recommendationDto = new RecommendationDto(1L, 2L, "Great work!", null);
        when(recommendationRepository.create(anyLong(), anyLong(), anyString())).thenReturn(1L); // Настраиваем поведение мока

        // Act: вызываем метод create
        RecommendationDto result = recommendationService.create(recommendationDto);

        // Assert: проверяем результат
        assertNotNull(result);
        assertEquals(1L, result.getId());

        // Verify: проверяем, что метод репозитория был вызван с правильными параметрами
        verify(recommendationRepository).create(1L, 2L, "Great work!");
    }
}