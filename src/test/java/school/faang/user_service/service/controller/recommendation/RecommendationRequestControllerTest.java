package school.faang.user_service.service.controller.recommendation;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.recommendation.RecommendationRequestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.exception.MessageRequestException;
import school.faang.user_service.service.RecommendationRequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestControllerTest {

    @Mock
    private RecommendationRequestService recommendationRequestService;

    @InjectMocks
    private RecommendationRequestController recommendationRequestController;

    @Test
    void testMessageNotNull() {
        Assert.assertThrows(MessageRequestException.class, ()-> recommendationRequestController.requestRecommendation(new RecommendationRequestDto(5L, null, "status", new ArrayList<SkillRequestDto>(), 5L, 6L, LocalDateTime.now(), LocalDateTime.now().plusMonths(7))));
    }

    @Test
    void testMessageNotBlank() {
        Assert.assertThrows(MessageRequestException.class, ()-> recommendationRequestController.requestRecommendation(new RecommendationRequestDto(5L, " ", "status", new ArrayList<SkillRequestDto>(), 5L, 6L, LocalDateTime.now(), LocalDateTime.now().plusMonths(7))));
    }

    @Test
    void testMessageNotEmpty() {
        Assert.assertThrows(MessageRequestException.class, ()-> recommendationRequestController.requestRecommendation(new RecommendationRequestDto(5L, "", "status", new ArrayList<SkillRequestDto>(), 5L, 6L, LocalDateTime.now(), LocalDateTime.now().plusMonths(7))));
    }
}
