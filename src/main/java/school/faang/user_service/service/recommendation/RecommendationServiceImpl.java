package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recomendation.PageDto;
import school.faang.user_service.dto.recomendation.RecommendationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

    public Long update(RecommendationDto recommendation) {
        log.info("Запрос на обновление рекомендации пользователю с ID: {}, от пользователя с ID: {}",
                recommendation.getReceiverId(),
                recommendation.getAuthorId());
        recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                recommendation.getAuthorId(),
                recommendation.getReceiverId()
        ).ifPresent(foundRecommendation -> {
            if (foundRecommendation.getCreatedAt().isBefore(LocalDateTime.now().minusMonths(6))) {
                throw new DataValidationException("Не прошло 6 месяцев с момента последней рекомендации!");
            }
        });
    }


    public void delete(long id) {
        log.info("Удаляем рекомендацию с ID: {}", id);
        recommendationRepository.deleteById(id);
    }

    public Page<RecommendationDto> getAllUserRecommendations(long receiverId, PageDto page) {
        log.info("Запрос всех рекомендаций пользователя с ID: {}", receiverId);
        Page<Recommendation> recommendations = recommendationRepository
                .findAllByReceiverId(receiverId, PageRequest.of(page.getPage(), page.getPageSize()));
        return recommendations.map(recommendationMapper::toDto);
    }

    public Page<RecommendationDto> getAllGivenRecommendations(long authorId, PageDto page) {
        log.info("Запрос на получение всех рекомендаций автора с ID: {}", authorId);
        Page<Recommendation> recommendations = recommendationRepository
                .findAllByAuthorId(authorId, PageRequest.of(page.getPage(), page.getPageSize()));
        return recommendations.map(recommendationMapper::toDto);
    }


}
