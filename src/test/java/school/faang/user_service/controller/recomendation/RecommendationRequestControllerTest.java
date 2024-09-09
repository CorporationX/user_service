package school.faang.user_service.controller.recomendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recomendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.recommendation.RecommendationRequestService;
import school.faang.user_service.validator.recommendation.RequestValidator;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestControllerTest {
    RecommendationRequestDto recommendationRequestDto;
    @Mock
    RequestValidator requestValidator;

    @Mock
    private RecommendationRequestService recommendationRequestService;

    @InjectMocks
    private RecommendationRequestController recommendationRequestController;

    @BeforeEach
    public void setUp() {
        recommendationRequestDto = new RecommendationRequestDto();
        requestValidator = new RequestValidator();
        recommendationRequestDto.setReceiverId(3L);
        recommendationRequestDto.setId(10L);
        recommendationRequestDto.setCreatedAt(LocalDateTime.now());
        recommendationRequestDto.setRequesterId(55L);
        recommendationRequestDto.setMessage("");
        recommendationRequestDto.setStatus(RequestStatus.PENDING);
    }

    @Test
    public void testRequestValidatorUnSuccess() {
        assertThrows(IllegalArgumentException.class, () -> {
            requestValidator.validateRecomendationRequest(recommendationRequestDto);
        });
    }

    @Test
    @DisplayName("Test success requestRecommendation")
    public void testRequestRecommendationSuccess() {
        when(recommendationRequestController.requestRecommendation(recommendationRequestDto)).thenReturn(recommendationRequestDto);
        recommendationRequestController.requestRecommendation(recommendationRequestDto);
        verify(recommendationRequestService, Mockito.times(1)).create(recommendationRequestDto);
    }

}