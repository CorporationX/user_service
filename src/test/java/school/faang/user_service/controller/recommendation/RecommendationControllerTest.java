package school.faang.user_service.controller.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;
import school.faang.user_service.validator.RecommendationValidator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationControllerTest {
    @InjectMocks
    private RecommendationController recommendationController;
    @Mock
    private RecommendationService recommendationService;
    @Mock
    private RecommendationValidator recommendationValidator;

    @Test
    public void testGiveRecommendation() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setContent("Sample content");

        when(recommendationService.create(recommendationDto)).thenReturn(recommendationDto);

        RecommendationDto result = recommendationController.giveRecommendation(recommendationDto);

        verify(recommendationValidator, times(1)).validateRecommendationContent(recommendationDto);
        verify(recommendationService, times(1)).create(recommendationDto);
        assertEquals(recommendationDto, result);
    }

    @Test
    public void testUpdateRecommendation() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setContent("Sample content");

        when(recommendationService.update(recommendationDto)).thenReturn(recommendationDto);

        RecommendationDto result = recommendationController.updateRecommendation(recommendationDto);

        verify(recommendationValidator, times(1)).validateRecommendationContent(recommendationDto);
        verify(recommendationService, times(1)).update(recommendationDto);
        assertEquals(recommendationDto, result);
    }

    @Test
    public void testDeleteRecommendation() {
        long recommendationId = 1L;

        recommendationController.deleteRecommendation(recommendationId);
        verify(recommendationService).delete(recommendationId);
    }

    @Test
    public void testGetAllUserRecommendations() {
        long userId = 1L;

        List<RecommendationDto> recommendationList = new ArrayList<>();
        RecommendationDto recommendation1 = new RecommendationDto();
        recommendation1.setId(1L);
        recommendation1.setAuthorId(2L);
        recommendation1.setReceiverId(userId);
        recommendation1.setContent("Content 1");

        RecommendationDto recommendation2 = new RecommendationDto();
        recommendation2.setId(2L);
        recommendation2.setAuthorId(3L);
        recommendation2.setReceiverId(userId);
        recommendation2.setContent("Content 2");

        recommendationList.add(recommendation1);
        recommendationList.add(recommendation2);

        when(recommendationService.getAllUserRecommendations(userId)).thenReturn(recommendationList);

        List<RecommendationDto> result = recommendationController.getAllUserRecommendations(userId);

        assertEquals(2, result.size());
        assertEquals(recommendation1.getId(), result.get(0).getId());
        assertEquals(recommendation2.getId(), result.get(1).getId());
    }

    @Test
    public void testGetAllGivenRecommendations() {
        long userId = 1L;

        List<RecommendationDto> recommendationList = new ArrayList<>();
        RecommendationDto recommendation1 = new RecommendationDto();
        recommendation1.setId(1L);
        recommendation1.setAuthorId(userId);
        recommendation1.setReceiverId(2L);
        recommendation1.setContent("Content 1");

        RecommendationDto recommendation2 = new RecommendationDto();
        recommendation2.setId(2L);
        recommendation2.setAuthorId(userId);
        recommendation2.setReceiverId(3L);
        recommendation2.setContent("Content 2");

        recommendationList.add(recommendation1);
        recommendationList.add(recommendation2);

        when(recommendationService.getAllGivenRecommendations(userId)).thenReturn(recommendationList);

        List<RecommendationDto> result = recommendationController.getAllGivenRecommendations(userId);

        verify(recommendationService, times(1)).getAllGivenRecommendations(userId);
        assertEquals(2, result.size());
        assertEquals(recommendation1.getId(), result.get(0).getId());
        assertEquals(recommendation2.getId(), result.get(1).getId());
    }
}