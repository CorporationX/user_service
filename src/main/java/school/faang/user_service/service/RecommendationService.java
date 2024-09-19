package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.validator.RecommendationDtoValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final RecommendationDtoValidator recommendationDtoValidator;
    private final SkillService skillService;
    private final SkillOfferService skillOfferService;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto create(RecommendationDto recommendation) {
        recommendationDtoValidator.validateRecommendation(recommendation);
        List<Skill> skills = getSkills(recommendation);

        Long recommendationId = recommendationRepository
                .create(recommendation.getAuthorId(), recommendation.getReceiverId(), recommendation.getContent());

        skillOfferService.addSkillsWithGuarantees(skills, recommendationId, recommendation);

        return recommendationMapper.toDto(getRecommendation(recommendationId));
    }

    public RecommendationDto update(RecommendationDto recommendation) {
        recommendationDtoValidator.validateRecommendation(recommendation);
        List<Skill> skills = getSkills(recommendation);

        recommendationRepository
                .update(recommendation.getAuthorId(), recommendation.getReceiverId(), recommendation.getContent());
        Recommendation existedRecommendation = getRecommendation(recommendation.getId());

        skillOfferService.deleteAllByRecommendationId(existedRecommendation.getId());

        skillOfferService.addSkillsWithGuarantees(skills, existedRecommendation.getId(), recommendation);

        return recommendationMapper.toDto(existedRecommendation);
    }

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long recieverId) {
        return recommendationMapper.toDtos(recommendationRepository
                .findAllByReceiverId(recieverId, Pageable.unpaged()).getContent());
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        return recommendationMapper.toDtos(recommendationRepository
                .findAllByAuthorId(authorId, Pageable.unpaged()).getContent());
    }

    private List<Skill> getSkills(RecommendationDto recommendation) {
        List<Long> skillIds = recommendation.getSkillOffers().stream()
                .map(SkillOfferDto::getSkillId)
                .toList();

        List<Skill> skills = skillService.getSkillByIds(skillIds);

        if (skillIds.size() != skills.size()) {
            throw new DataValidationException("Not all skills exist!");
        }

        return skills;
    }

    private Recommendation getRecommendation(Long recommendationId) {
        return recommendationRepository.findById(recommendationId).orElseThrow(
                () -> new DataValidationException("Recommendation with id - " + recommendationId + " does not exist"));
    }
}
