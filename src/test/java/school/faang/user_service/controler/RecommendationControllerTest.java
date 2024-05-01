package school.faang.user_service.controler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationControllerTest {
    @Mock
    private RecommendationService recommendationService;

    @InjectMocks
    private RecommendationController recommendationController;

    @Test
    public void testRecommendationValidationWithNullContent() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setContent(null);

        assertThrows(DataValidationException.class, () -> recommendationController.giveRecommendation(recommendationDto));
    }

    @Test
    public void testRecommendationValidationWithEmptyContent() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setContent("      ");

        assertThrows(DataValidationException.class, () -> recommendationController.giveRecommendation(recommendationDto));
    }

    @Test
    public void testIdValidation() {
        long receiverId = 0;
        int numPage = 0;
        int sizePage = 10;

        assertThrows(DataValidationException.class, () -> recommendationController.getAllUserRecommendations(receiverId, numPage, sizePage));
    }

    @Test
    public void testGiveRecommendationCorrectInput() {
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
        recommendationDto.setContent("content");

        when(recommendationService.update(recommendationDto)).thenReturn(recommendationDto);

        RecommendationDto result = recommendationController.updateRecommendation(recommendationDto);

        verify(recommendationService, times(1)).update(recommendationDto);
        assertEquals(recommendationDto, result);
    }

    @Test
    public void testDeleteRecommendation() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);
        recommendationDto.setContent("content");

        doNothing().when(recommendationService).delete(anyLong());
        recommendationController.deleteRecommendation(recommendationDto);

        verify(recommendationService).delete(recommendationDto.getId());
    }

    @Test
    public void testGetAllUserRecommendation() {
        long userId = 1L;
        int pageNum = 0;
        int pageSize = 10;

        RecommendationDto recommendationDto1 = new RecommendationDto();
        recommendationDto1.setId(1L);
        RecommendationDto recommendationDto2 = new RecommendationDto();
        recommendationDto2.setId(2L);
        List<RecommendationDto> recommendationDtos = List.of(recommendationDto1, recommendationDto2);

        Page<RecommendationDto> page = new PageImpl<>(recommendationDtos, PageRequest.of(pageNum, pageSize), recommendationDtos.size());

        when(recommendationService.getAllUserRecommendation(userId, pageNum, pageSize)).thenReturn(page);
        Page<RecommendationDto> resultPage = recommendationController.getAllUserRecommendations(userId, pageNum, pageSize);

        assertEquals(recommendationDtos, resultPage.getContent());
    }

    @Test
    public void testGetAllRecommendation() {
        long userId = 1L;
        int pageNum = 0;
        int pageSize = 10;

        RecommendationDto recommendationDto1 = new RecommendationDto();
        recommendationDto1.setId(1L);
        RecommendationDto recommendationDto2 = new RecommendationDto();
        recommendationDto2.setId(2L);
        List<RecommendationDto> recommendationDtos = List.of(recommendationDto1, recommendationDto2);

        Page<RecommendationDto> page = new PageImpl<>(recommendationDtos, PageRequest.of(pageNum, pageSize), recommendationDtos.size());

        when(recommendationService.getAllRecommendation(userId, pageNum, pageSize)).thenReturn(page);
        Page<RecommendationDto> resultPage = recommendationController.getAllRecommendation(userId, pageNum, pageSize);

        assertEquals(recommendationDtos, resultPage.getContent());
    }
}