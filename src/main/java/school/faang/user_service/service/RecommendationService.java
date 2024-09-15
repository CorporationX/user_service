package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto create(RecommendationDto recommendation) {
        validateRecommendationContentIsBlank(recommendation);

        validateRecommendationForPeriod(recommendation);

        validateRecommendationSkillInSystem(recommendation);

        List<Long> skillIds = recommendation.getSkillOffers().stream()
                .map(SkillOfferDto::getSkillId).toList();

        Long recommendationId = recommendationRepository.create(recommendation.getAuthorId(), recommendation.getReceiverId(), recommendation.getContent());
        saveSkillOffers(recommendationId, skillIds);

        return recommendation;
    }

    @Transactional
    public RecommendationDto update(RecommendationDto recommendation) {
        validateRecommendationContentIsBlank(recommendation);

        validateRecommendationSkillInSystem(recommendation);

        recommendationRepository.update(recommendation.getAuthorId(), recommendation.getReceiverId(),
                recommendation.getContent());
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());

        List<Long> skillIds = recommendation.getSkillOffers().stream()
                .map(SkillOfferDto::getSkillId)
                .distinct().toList();

        for (Long skillId : skillIds) {
            skillOfferRepository.create(skillId, recommendation.getId());
        }
        return recommendation;
    }

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        Page<Recommendation> pageRequest = recommendationRepository.findAllByReceiverId(receiverId, Pageable.unpaged());
        List<Recommendation> recommendations = pageRequest.getContent();

        return recommendations.stream().map(recommendation -> recommendationMapper.toDto(recommendation)).toList();
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        Page<Recommendation> pageRequest = recommendationRepository.findAllByAuthorId(authorId, Pageable.unpaged());
        List<Recommendation> recommendations = pageRequest.getContent();

        return recommendations.stream().map(recommendationMapper::toDto).toList();
    }

    private void validateRecommendationContentIsBlank(RecommendationDto recommendation) {
        if (recommendation.getContent() == null || recommendation.getContent().isBlank()) {
            throw new DataValidationException("Recommendation text cannot be empty.");
        }
    }

    private void validateRecommendationForPeriod(RecommendationDto recommendation) {
        Optional<LocalDateTime> lastRecommendationDate = getLastRecommendationDate(recommendation.getAuthorId(), recommendation.getReceiverId());
        lastRecommendationDate.ifPresent(date -> {
            if (date.plusMonths(6).isAfter(LocalDate.now().atStartOfDay())) {
                throw new DataValidationException("It is impossible to make a recommendation before 6 months have passed");
            }
        });
    }

    private void validateRecommendationSkillInSystem(RecommendationDto recommendation) {
        List<Long> skillIds = recommendation.getSkillOffers().stream()
                .map(SkillOfferDto::getSkillId)
                .distinct().toList();
        List<Skill> skillsFromDb = skillRepository.findAllById(skillIds);

        if (skillsFromDb.size() != skillIds.size()) {
            throw new DataValidationException("You offer skills that don't exist in the system.");
        }
    }

    private Optional<LocalDateTime> getLastRecommendationDate(Long authorId, Long receiverId) {
        Optional<Recommendation> lastRecommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId);
        if (lastRecommendation.isPresent()) {
            LocalDateTime date = lastRecommendation.get().getCreatedAt();
            return Optional.ofNullable(date);
        } else {
            return Optional.empty();
        }
    }

    public boolean receiverHasSkill(long receiverId, long skillId) {
        Optional<Skill> receiverSkill = skillRepository.findUserSkill(receiverId, skillId);

        return receiverSkill.isPresent();
    }

    private void saveSkillOffers(Long recommendationId, List<Long> skillIds) {
        for (Long skillId : skillIds) {
            skillOfferRepository.create(skillId, recommendationId);
        }
    }

}
