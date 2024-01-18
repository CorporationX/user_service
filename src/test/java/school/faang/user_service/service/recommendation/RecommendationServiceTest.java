package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private RecommendationMapper recommendationMapper;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    public void testGetAllUserRecommendations() {
        long receiverId = 1L;

        Recommendation recommendation1 = new Recommendation();
        Recommendation recommendation2 = new Recommendation();
        List<Recommendation> recommendations = Stream.of(recommendation1, recommendation2).collect(Collectors.toList());

        when(recommendationRepository.findAllByReceiverId(receiverId)).thenReturn(recommendations);

        RecommendationDto recommendationDto1 = new RecommendationDto();
        RecommendationDto recommendationDto2 = new RecommendationDto();
        List<RecommendationDto> expectedDtos = Stream.of(recommendationDto1, recommendationDto2).collect(Collectors.toList());

        when(recommendationMapper.toDto(recommendation1)).thenReturn(recommendationDto1);
        when(recommendationMapper.toDto(recommendation2)).thenReturn(recommendationDto2);

        List<RecommendationDto> actualDtos = recommendationService.getAllUserRecommendations(receiverId);

        assertEquals(expectedDtos, actualDtos);

        verify(recommendationRepository, times(1)).findAllByReceiverId(receiverId);

        verify(recommendationMapper, times(1)).toDto(recommendation1);
        verify(recommendationMapper, times(1)).toDto(recommendation2);
    }
}