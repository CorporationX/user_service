package school.faang.user_service.service;

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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private RecommendationMapper recommendationMapper;

    @InjectMocks
    private RecommendationService recommendationService;

    private AutoCloseable closeable;


    @Test
    void testGetAllUserRecommendations() {
        long receiverId = 1L;

        Recommendation recommendationEntity1 = new Recommendation();
        Recommendation recommendationEntity2 = new Recommendation();
        List<Recommendation> recommendationEntities = Arrays.asList(recommendationEntity1, recommendationEntity2);

        Page<Recommendation> recommendationPage = new PageImpl<>(recommendationEntities);

        when(recommendationRepository.findAllByReceiverId(eq(receiverId), any(Pageable.class)))
                .thenReturn(recommendationPage);

        when(recommendationMapper.toDto(any(Recommendation.class)))
                .thenReturn(new RecommendationDto(receiverId));

        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(receiverId);

        assertEquals(recommendationEntities.size(), result.size());

        verify(recommendationMapper, times(recommendationEntities.size())).toDto(any(Recommendation.class));

    }
}