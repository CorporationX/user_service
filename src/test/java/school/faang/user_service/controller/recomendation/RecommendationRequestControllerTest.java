package school.faang.user_service.controller.recomendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.RecommendationRequestController;
import school.faang.user_service.model.dto.RecommendationRequestDto;
import school.faang.user_service.model.enums.RequestStatus;
import school.faang.user_service.service.impl.RecommendationRequestServiceImpl;
import school.faang.user_service.validator.RequestValidator;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestControllerTest {
   private RecommendationRequestDto recommendationRequestDto;
    @Mock
   private RequestValidator requestValidator;

    @Mock
    private RecommendationRequestServiceImpl recommendationRequestService;

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
    public void testRequestRecommendationSuccess() {
        when(recommendationRequestController.requestRecommendation(recommendationRequestDto)).thenReturn(recommendationRequestDto);
        recommendationRequestController.requestRecommendation(recommendationRequestDto);
        verify(recommendationRequestService, Mockito.times(1)).create(recommendationRequestDto);
    }
}