package school.faang.user_service.service.recommendation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final RecommendationMapper recommendationMapper;

    @Override
    public RecommendationDto create(RecommendationDto recommendation) {
        validateSkillInSystem(recommendation);

        validateRecommendationForPeriod(recommendation);

        List<Long> skillIds = recommendation.getSkillOffers().stream()
                .map(SkillOfferDto::getSkillId)
                .toList();

        Long recommendationId = recommendationRepository.create(recommendation.getAuthorId(), recommendation.getReceiverId(), recommendation.getContent());
        saveSkillOffers(recommendationId, skillIds);

        return recommendation;
    }

    @Override
    @Transactional
    public RecommendationDto update(RecommendationDto recommendation) {
        validateSkillInSystem(recommendation);

        recommendationRepository.update(recommendation.getAuthorId(), recommendation.getReceiverId(),
                recommendation.getContent());
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());

        List<Long> skillIds = getSkillIds(recommendation);

        for (Long skillId : skillIds) {
            skillOfferRepository.create(skillId, recommendation.getId());
        }
        return recommendation;
    }

    @Override
    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    @Override
    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        Page<Recommendation> pageRequest = recommendationRepository.findAllByReceiverId(receiverId, Pageable.unpaged());
        List<Recommendation> recommendations = pageRequest.getContent();

        return recommendations.stream()
                .map(recommendation -> recommendationMapper.toDto(recommendation))
                .toList();
    }

    @Override
    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        Page<Recommendation> pageRequest = recommendationRepository.findAllByAuthorId(authorId, Pageable.unpaged());
        List<Recommendation> recommendations = pageRequest.getContent();

        return recommendations.stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    public void validateSkillInSystem(RecommendationDto recommendation) {
        if (recommendation.getContent().isBlank()) {
            throw new DataValidationException("Recommendation text cannot be empty");
        }

        List<Long> skillIds = getSkillIds(recommendation);
        List<Skill> skillsFromDb = skillRepository.findAllById(skillIds);

        if (skillsFromDb.size() != skillIds.size()) {
            throw new DataValidationException("You offer skills that don't exist in the system.");
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

    private List<Long> getSkillIds(RecommendationDto recommendation) {
        return recommendation.getSkillOffers().stream()
                .map(SkillOfferDto::getSkillId)
                .toList();
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

    private void saveSkillOffers(Long recommendationId, List<Long> skillIds) {
        for (Long skillId : skillIds) {
            skillOfferRepository.create(skillId, recommendationId);
        }
    }

}