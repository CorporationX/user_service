package school.faang.user_service.service.recomendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.kafka.RecommendationEvent;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.kafka.producer.RecommendationProducer;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    private final RecommendationMapper recommendationMapper;
    private final RecommendationRepository recommendationRepository;
    private final RecommendationProducer recommendationProducer;

    @Override
    public RecommendationDto create(long authorId, CreateRecommendationDto createRecommendationDto) {
        Recommendation newRecommendation = recommendationMapper.toRecommendation(createRecommendationDto);
        Recommendation savedRecommendation = recommendationRepository.save(newRecommendation);
        log.info("Создана новая рекоммендация с id: {}.", savedRecommendation.getId());
        RecommendationEvent newRecommendationEvent = new RecommendationEvent(
                savedRecommendation.getId(),
                authorId,
                createRecommendationDto.receiverId());
        recommendationProducer.sendToKafka(newRecommendationEvent);
        log.info("Информация о новой рекомендации с id: {} "
                        + "отправлена из recommendationService в RecommendationProducer",
                savedRecommendation.getId());
        return recommendationMapper.toRecommendationDto(savedRecommendation);
    }
}
