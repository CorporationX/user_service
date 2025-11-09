package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.kafka.RecommendationEvent;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.kafka.producer.RecommendationProducer;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.service.recomendation.RecommendationServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceImplTest {
    @InjectMocks
    private RecommendationServiceImpl recommendationServiceImpl;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private RecommendationProducer recommendationProducer;

    @Spy
    private final RecommendationMapper recommendationMapper = Mappers.getMapper(RecommendationMapper.class);

    @Test
    public void createSuccessfullyCreates() {
        long anyLong = 1L;
        String anyString = "anyString";
        CreateRecommendationDto anyCreateRecommendationDto = new CreateRecommendationDto(anyLong, anyString);
        Recommendation anyRecommendation = new Recommendation();
        anyRecommendation.setId(anyLong);
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(anyRecommendation);

        recommendationServiceImpl.create(anyLong, anyCreateRecommendationDto);

        verify(recommendationMapper, times(1))
                .toRecommendation(any(CreateRecommendationDto.class));
        verify(recommendationRepository, times(1)).save(any(Recommendation.class));
        verify(recommendationProducer).sendToKafka(any(RecommendationEvent.class));
        verify(recommendationMapper, times(1)).toRecommendationDto(any(Recommendation.class));
    }
}
