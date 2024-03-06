package school.faang.user_service.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
    void testGetAllUserRecommendations() {
        long receiverId = 1L;

        Recommendation recommendationEntity1 = new Recommendation();
        Recommendation recommendationEntity2 = new Recommendation();
        RecommendationDto result = RecommendationDto.builder().receiverId(receiverId).build();
        List<Recommendation> recommendationEntities = Arrays.asList(recommendationEntity1, recommendationEntity2);

        Page<Recommendation> recommendationPage = new PageImpl<>(recommendationEntities);

        when(recommendationRepository.findAllByReceiverId(eq(receiverId), any(Pageable.class)))
                .thenReturn(recommendationPage);

        when(recommendationMapper.toDto(any(Recommendation.class)))
                .thenReturn(result);

        List<RecommendationDto> resultRecs = recommendationService.getAllUserRecommendations(receiverId);

        assertEquals(recommendationEntities.size(), resultRecs.size());

        verify(recommendationMapper, times(recommendationEntities.size())).toDto(any(Recommendation.class));

    }

    @Test
    public void testDeleteRecommendation() {
        long id = 1L;
        recommendationService.delete(id);
        Mockito.verify(recommendationRepository, Mockito.times(1)).deleteById(id);
    }

    @Test
    public void testGetAllGivenRecommendations() {
        when(recommendationRepository.findAllByAuthorId(1L)).thenReturn(Arrays.asList(recommendation));
        List<RecommendationDto> recommendationDtos = recommendationService.getAllGivenRecommendations(1L);
        assertEquals(1, recommendationDtos.size());
        assertEquals(recommendationDto, recommendationDtos.get(0));
    }
}