package school.faang.user_service.service.controller.recommendation;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.recommendation.RecommendationRequestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.service.RecommendationRequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestControllerTest {

    @Mock
    private RecommendationRequestService recommendationRequestService;

    @InjectMocks
    private RecommendationRequestController recommendationRequestController;

    @Test
    void messageNotNullTest() {
        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto(5L, null, "status", new ArrayList<SkillRequestDto>(), 5L, 6L, LocalDateTime.now(), LocalDateTime.now().plusMonths(7));
        boolean exception = false;

        try {
            recommendationRequestController.requestRecommendation(recommendationRequestDto);
        } catch (IllegalArgumentException e) {
            exception = true;
        }

        assertTrue(exception);
    }

    @Test
    void messageNotBlankTest() {
        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto(5L, " ", "status", new ArrayList<SkillRequestDto>(), 5L, 6L, LocalDateTime.now(), LocalDateTime.now().plusMonths(7));
        boolean exception = false;

        try {
            recommendationRequestController.requestRecommendation(recommendationRequestDto);
        } catch (IllegalArgumentException e) {
            exception = true;
        }

        assertTrue(exception);
    }

    @Test
    void messageNotEmptyTest() {
        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto(5L, "", "status", new ArrayList<SkillRequestDto>(), 5L, 6L, LocalDateTime.now(), LocalDateTime.now().plusMonths(7));
        boolean exception = false;

        try {
            recommendationRequestController.requestRecommendation(recommendationRequestDto);
        } catch (IllegalArgumentException e) {
            exception = true;
        }

        assertTrue(exception);
    }
}
