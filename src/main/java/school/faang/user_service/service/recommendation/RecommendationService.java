package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.recommendation.RecommendationServiceValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final RecommendationServiceValidator validator;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto create(RecommendationDto recommendation) {
        validator.validateDaysBetweenRecommendations(recommendation);
        validator.validateSkillOffers(recommendation);

        recommendation.skillOffers()
                .stream()
                .map(SkillOfferDto::skillId)
                .filter(skillId -> skillRepository
                        .findUserSkill(skillId, recommendation.receiverId()).isEmpty())
                .forEach(skillId -> skillOfferRepository.create(skillId, recommendation.id()));

        return recommendation;
    }

    public RecommendationDto update(RecommendationDto recommendationDto) {
        validator.validateDaysBetweenRecommendations(recommendationDto);
        validator.validateSkillOffers(recommendationDto);

        skillOfferRepository.deleteAllByRecommendationId(recommendationDto.id());

        recommendationDto
                .skillOffers()
                .forEach(skillOfferDto -> skillOfferRepository
                        .create(skillOfferDto.skillId(), recommendationDto.id()));

        return recommendationDto;
    }

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        Page<Recommendation> recommendations = recommendationRepository
                .findAllByReceiverId(receiverId, Pageable.unpaged());

        return recommendations.map(recommendationMapper::toDto).toList();
    }


    public List<RecommendationDto> getAllGivenRecommendations(long receiverId) {
        Page<Recommendation> recommendations = recommendationRepository
                .findAllByAuthorId(receiverId, Pageable.unpaged());

        return recommendations.map(recommendationMapper::toDto).toList();
    }
}
