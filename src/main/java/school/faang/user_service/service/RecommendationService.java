package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validation.RecommendationValidation;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationValidation recommendationValidation;

    public RecommendationDto create(RecommendationDto recommendation) {
        recommendationValidation.validateOfLatestRecommendation(recommendation);
        recommendationValidation.validateOfSkills(recommendation.getSkillOffers());
        saveSkillOffers(recommendation);
        recommendationRepository.create(recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent());
        return recommendation;
    }

    public RecommendationDto update(RecommendationDto recommendation) {
        recommendationValidation.validateOfLatestRecommendation(recommendation);
        recommendationValidation.validateOfSkills(recommendation.getSkillOffers());
        recommendationRepository.update(recommendation.getAuthorId(), recommendation.getReceiverId(),
                recommendation.getContent());
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        return saveSkillOffers(recommendation);
    }

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        Page<Recommendation> recommendations = recommendationRepository
                .findAllByReceiverId(receiverId, Pageable.unpaged());

        return recommendations.stream()
                .map(recommendation -> recommendationMapper.toDto(recommendation))
                .toList();
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        Page<Recommendation> recommendations = recommendationRepository.findAllByAuthorId(authorId, Pageable.unpaged());
        return recommendations.stream()
                .map(recommendation -> recommendationMapper.toDto(recommendation))
                .toList();
    }

    private RecommendationDto saveSkillOffers(RecommendationDto recommendation) {
        for (SkillOfferDto skillOfferDto : recommendation.getSkillOffers()) {
            skillOfferRepository.create(skillOfferDto.getId(), recommendation.getId());
        }
        return recommendation;
    }
}

