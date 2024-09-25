package school.faang.user_service.controller.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.service.recommendation.RecommendationService;
import school.faang.user_service.test_data.recommendation.TestDataRecommendation;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationControllerTest {
    @Mock
    private RecommendationService recommendationService;
    @Mock
    private RecommendationMapper recommendationMapper;
    @InjectMocks
    private RecommendationController recommendationController;

    private User author;
    private User receiver;
    private Recommendation recommendation;
    private RecommendationDto recommendationDto;
    private int offset;
    private int limit;

    @BeforeEach
    void setUp() {
        TestDataRecommendation testDataRecommendation = new TestDataRecommendation();

        author = testDataRecommendation.getAuthor();
        receiver = testDataRecommendation.getReceiver();
        recommendation = testDataRecommendation.getRecommendation();
        recommendationDto = testDataRecommendation.getRecommendationDto();
    }

    @Test
    void testGiveRecommendationSuccess() {
        when(recommendationService.createRecommendation(recommendationDto)).thenReturn(recommendationDto);

        RecommendationDto result = recommendationController.giveRecommendation(recommendationDto);
        assertNotNull(result);
        assertEquals(recommendationDto, result);

        verify(recommendationService, atLeastOnce()).createRecommendation(recommendationDto);
    }


    @Test
    void testUpdateRecommendationSuccess() {
        when(recommendationService.updateRecommendation(recommendationDto)).thenReturn(recommendationDto);

        RecommendationDto result = recommendationController.updateRecommendation(recommendationDto);
        assertNotNull(result);
        assertEquals(recommendationDto, result);

        verify(recommendationService, atLeastOnce()).updateRecommendation(recommendationDto);
    }

    @Test
    void testDeleteRecommendationSuccess() {
        recommendationController.deleteRecommendation(recommendationDto.getId());

        verify(recommendationService, atLeastOnce()).deleteRecommendation(recommendationDto.getId());
    }

    @Test
    void testGetAllUserRecommendationsSuccess() {
        offset = 0;
        limit = 10;
        List<Recommendation> recommendationList = List.of(recommendation);
        List<RecommendationDto> recommendationDtoList = List.of(recommendationDto);

        when(recommendationService.getAllUserRecommendations(receiver.getId(), offset, limit))
                .thenReturn(recommendationList);

        when(recommendationMapper.toDto(recommendation)).thenReturn(recommendationDto);

        List<RecommendationDto> result = recommendationController.getAllUserRecommendations(receiver.getId(), offset, limit);
        assertNotNull(result);
        assertEquals(recommendationDtoList, result);

        verify(recommendationService, atLeastOnce()).getAllUserRecommendations(receiver.getId(), offset, limit);
    }

    @Test
    void testGetAllGivenRecommendationsSuccess() {
        offset = 0;
        limit = 10;
        List<Recommendation> recommendationList = List.of(recommendation);
        List<RecommendationDto> recommendationDtoList = List.of(recommendationDto);

        when(recommendationService.getAllGivenRecommendations(author.getId(), offset, limit))
                .thenReturn(recommendationList);

        when(recommendationMapper.toDto(recommendation)).thenReturn(recommendationDto);

        List<RecommendationDto> result = recommendationController.getAllGivenRecommendations(author.getId(), offset, limit);
        assertNotNull(result);
        assertEquals(recommendationDtoList, result);

        verify(recommendationService, atLeastOnce()).getAllGivenRecommendations(author.getId(), offset, limit);
    }
}
