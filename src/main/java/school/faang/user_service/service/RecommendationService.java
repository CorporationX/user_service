package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;

    @Transactional(readOnly = true)
    public Page<RecommendationDto> getAllReceiverRecommendations(Long receiverId, int page, int pageSize) {
        Page<Recommendation> recommendations = recommendationRepository.findAllByReceiverId(
                receiverId,
                PageRequest.of(page, pageSize)
        );
        return recommendations.map(recommendationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<RecommendationDto> getAllAuthorRecommendations(Long authorId, int page, int pageSize) {
        Page<Recommendation> recommendationPage = recommendationRepository.findAllByAuthorId(
                authorId,
                PageRequest.of(page, pageSize)
        );
        return recommendationPage.map(recommendationMapper::toDto);
    }
}
