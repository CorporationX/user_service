package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.service.skill.SkillOfferService;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.validator.recommendation.RecommendationDtoValidator;

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

    public RecommendationDto update(long id, RecommendationDto recommendation) {
        recommendationDtoValidator.validateRecommendation(recommendation);
        List<Skill> skills = getSkills(recommendation);

        recommendationRepository
                .update(recommendation.getAuthorId(), recommendation.getReceiverId(), recommendation.getContent());

        skillOfferService.deleteAllByRecommendationId(id);

        skillOfferService.addSkillsWithGuarantees(skills, id, recommendation);

        return recommendationMapper.toDto(getRecommendation(id));
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
