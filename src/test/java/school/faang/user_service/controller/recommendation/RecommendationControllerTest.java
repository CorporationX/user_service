package school.faang.user_service.controller.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationControllerTest {
    @InjectMocks
    private RecommendationController recommendationController;
    @Mock
    private RecommendationService recommendationService;

    @Test
    public void testGiveRecommendation() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setContent("content");

        when(recommendationService.create(recommendationDto)).thenReturn(recommendationDto);

        RecommendationDto result = recommendationController.giveRecommendation(recommendationDto);

        verify(recommendationService, times(1)).create(recommendationDto);
        assertEquals(recommendationDto, result);
    }

    @Test
    public void testUpdateRecommendation() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setContent("Sample content");

        when(recommendationService.update(recommendationDto)).thenReturn(recommendationDto);

        RecommendationDto result = recommendationController.updateRecommendation(recommendationDto);

        verify(recommendationService, times(1)).update(recommendationDto);
        assertEquals(recommendationDto, result);
    }

    @Test
    public void testGetAllUserRecommendations() {
        long userId = 1L;
        int pageNumber = 0;
        int pageSize = 10;

        RecommendationDto recommendationDto1 = new RecommendationDto();
        recommendationDto1.setId(1L);
        RecommendationDto recommendationDto2 = new RecommendationDto();
        recommendationDto2.setId(2L);
        List<RecommendationDto> recommendationDtosList = List.of(recommendationDto1, recommendationDto2);

        Page<RecommendationDto> page = new PageImpl<>(recommendationDtosList, PageRequest.of(pageNumber, pageSize), recommendationDtosList.size());

        when(recommendationService.getAllUserRecommendations(userId, pageNumber, pageSize)).thenReturn(page);

        Page<RecommendationDto> resultPage = recommendationController.getAllUserRecommendations(userId, pageNumber, pageSize);

        assertEquals(recommendationDtosList, resultPage.getContent());
    }

    @Test
    public void testGetAllGivenRecommendations() {
        long userId = 1L;
        int pageNumber = 0;
        int pageSize = 10;

        RecommendationDto recommendationDto1 = new RecommendationDto();
        recommendationDto1.setId(1L);
        RecommendationDto recommendationDto2 = new RecommendationDto();
        recommendationDto2.setId(2L);
        List<RecommendationDto> recommendationDtosList = List.of(recommendationDto1, recommendationDto2);

        Page<RecommendationDto> page = new PageImpl<>(recommendationDtosList, PageRequest.of(pageNumber, pageSize), recommendationDtosList.size());

        when(recommendationService.getAllGivenRecommendations(userId, pageNumber, pageSize)).thenReturn(page);

        Page<RecommendationDto> resultPage = recommendationController.getAllGivenRecommendations(userId, pageNumber, pageSize);

        assertEquals(recommendationDtosList, resultPage.getContent());
    }
}
