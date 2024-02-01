package school.faang.user_service.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.RecommendationMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    @Mock
    private RecommendationRepository recommendationRepository;
    @Spy
    private RecommendationMapperImpl recommendationMapper;

    @InjectMocks
    private RecommendationService recommendationService;
    private Recommendation recommendation;
    private RecommendationDto recommendationDto;

    @BeforeEach
    public void init() {
        recommendation = new Recommendation();
        recommendationDto = new RecommendationDto();
        }

    @Test
    public void testGetAllGivenRecommendations() {
        when(recommendationRepository.findAllByAuthorId(1L)).thenReturn(Arrays.asList(recommendation));
        List<RecommendationDto> recommendationDtos = recommendationService.getAllGivenRecommendations(1L);
        assertEquals(1, recommendationDtos.size());
        assertEquals(recommendationDto, recommendationDtos.get(0));
    }
}
