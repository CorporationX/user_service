package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @InjectMocks
    private RecommendationService recommendationService;
    private Page<Recommendation> page;

    @BeforeEach
    public void init() {
        Recommendation recommendation = new Recommendation();
        page = new PageImpl<>(List.of(recommendation));
    }
    @Test
    public void testGetAllGivenRecommendations() {
        when(recommendationRepository.findAllByAuthorId(1L, Pageable.unpaged()))
              .thenReturn(page);
        List<RecommendationDto> recommendationDtos = recommendationService.getAllGivenRecommendations(1L);
        assertEquals(1, recommendationDtos.size());
    }


}