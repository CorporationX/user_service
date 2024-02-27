package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recomendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;

    public void create(RecommendationDto recommendation) {
//         authorId = recommendation.getAuthorId();

    }

    public Long update(RecommendationDto updated) {

    }

    public void delete(long id) {
        log.info("Удаляем рекомендацию с ID: {}", id);
        recommendationRepository.deleteById(id);

    }

    public Page<RecommendationDto> getAllUserRecommendations(long receiverId, int page, int pageSize) {
        log.info("Запрос всех рекомендаций пользователя с ID: {}", receiverId);
        Page<Recommendation> recommendations = recommendationRepository
                .findAllByReceiverId(receiverId, PageRequest.of(page, pageSize));
        return recommendations.map(recommendationMapper::toDto);
    }

    public Page<RecommendationDto> getAllGivenRecommendations(long authorId, int page, int pageSize) {
        log.info("Запрос на получение всех реомендаций автора с ID: {}", authorId);
        Page<Recommendation> recommendations = recommendationRepository
                .findAllByAuthorId(authorId, PageRequest.of(page, pageSize));
        return recommendations.map(recommendationMapper::toDto);
    }


}
