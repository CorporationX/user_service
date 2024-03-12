package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;


@Service
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    // private final SkillMapper skillMapper;

    @Autowired
    public RecommendationRequestService(RecommendationRequestRepository recommendationRequestRepository, SkillRequestRepository skillRequestRepository) {
        this.recommendationRequestRepository = recommendationRequestRepository;
        this.skillRequestRepository = skillRequestRepository;
       // this.skillMapper = skillMapper;
    }

    @Transactional
    public void create(Long requesterId, Long receiverId, List<String> skills) {
        boolean requestExists = recommendationRequestRepository.existsByRequesterIdAndReceiverId(requesterId, receiverId);

        if (requestExists) {
            throw new IllegalArgumentException("Запрос на рекомендацию уже отправлен и еще не закрыт");
        }

        recommendationRequestRepository.createRequest(requesterId, receiverId);

        for (String skill : skills) {
            Long skillId = Long.valueOf(skill);
            skillRequestRepository.create(requesterId, skillId);
        }
    }
}
