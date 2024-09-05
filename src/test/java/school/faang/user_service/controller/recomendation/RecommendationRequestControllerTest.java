package school.faang.user_service.controller.recomendation;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recomendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.recomendation.RecommendationRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Component
@RequiredArgsConstructor
class RecommendationRequestControllerTest {
    static RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto();

    @BeforeAll
    public static void setUp() {
        recommendationRequestDto.setReceiverId(3L);
        recommendationRequestDto.setId(10L);
        recommendationRequestDto.setCreatedAt(LocalDateTime.now());
        recommendationRequestDto.setRequesterId(55L);
        recommendationRequestDto.setMessage("");
        recommendationRequestDto.setStatus(RequestStatus.PENDING);
        recommendationRequestDto.setSkillsId(List.of(5L, 56L, 53L, 10L));
    }

    @Mock
    private RecommendationRequestService recommendationRequestService = Mockito.mock(RecommendationRequestService.class);


    @InjectMocks
    private final RecommendationRequestController recommendationRequestController = new RecommendationRequestController(recommendationRequestService);

    @Test
    public void testCreateRecommendationRequestDto() {
        recommendationRequestDto.setMessage("testMessage");
        Mockito.when(recommendationRequestController.requestRecommendation(recommendationRequestDto)).thenReturn(recommendationRequestDto);
        recommendationRequestController.requestRecommendation(recommendationRequestDto);
        Mockito.verify(recommendationRequestService, Mockito.times(1)).create(recommendationRequestDto);
    }

    @Test
    @DisplayName("Test for null dto")
    public void testNullRecommendationRequestDto() {
        assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestController.requestRecommendation(null);
        });
    }

    @Test
    @DisplayName("Test for empty message")
    public void testEmptyMessageRecommendationRequestDto() {
        assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestController.requestRecommendation(recommendationRequestDto);
        });
    }
}