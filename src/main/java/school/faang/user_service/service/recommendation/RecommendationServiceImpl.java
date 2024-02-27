package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recomendation.RecommendationDto;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;

    public void create(RecommendationDto recommendation) {
//         authorId = recommendation.getAuthorId();

    }

    public void delete(long id) {
        log.info("Удаляем рекомендацию с ID: {}", id);
        recommendationRepository.deleteById(id);

    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        log.info("Запрос всех рекомендаций пользователя с ID: {}", receiverId);
        recommendationRepository.findAllByReceiverId(receiverId,  );
    }

}
