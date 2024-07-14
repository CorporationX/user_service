package school.faang.service.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import school.faang.service.user.dto.recommendation.RecommendationDto;
import school.faang.service.user.entity.Skill;
import school.faang.service.user.entity.UserSkillGuarantee;
import school.faang.service.user.entity.recommendation.Recommendation;
import school.faang.service.user.entity.recommendation.SkillOffer;
import school.faang.service.user.mapper.RecommendationMapper;
import school.faang.service.user.repository.SkillRepository;
import school.faang.service.user.repository.UserSkillGuaranteeRepository;
import school.faang.service.user.repository.recommendation.RecommendationRepository;
import school.faang.service.user.repository.recommendation.SkillOfferRepository;
import school.faang.service.user.utils.RecommendationValidator;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillRepository skillRepository;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationValidator recommendationValidator;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);

        recommendationValidator.validate(recommendation);

        long recommendationId = recommendationRepository.create(
                recommendation.getAuthor().getId(),
                recommendation.getReceiver().getId(),
                recommendation.getContent()
        );

        List<Long> offeredSkillIds = recommendation.getSkillOffers().stream()
                .map(SkillOffer::getSkill)
                .map(Skill::getId)
                .distinct()
                .toList();

        addUserSkillGuarantee(offeredSkillIds, recommendation);

        Optional<Recommendation> createdRecommendation = recommendationRepository.findById(recommendationId);

        if (createdRecommendation.isEmpty()) {
            throw new ServiceException("Could not save recommendation");
        }

        addSkillOffersToRecommendation(offeredSkillIds, createdRecommendation.get());

        return recommendationMapper.toDto(createdRecommendation.get());
    }

    @Transactional
    public RecommendationDto update(RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);

        recommendationValidator.validate(recommendation);

        recommendationRepository.update(
                recommendation.getId(),
                recommendation.getContent()
        );

        skillOfferRepository.deleteAllByRecommendationId(recommendationDto.id());

        List<Long> offeredSkillIds = recommendation.getSkillOffers().stream()
                .map(SkillOffer::getSkill)
                .map(Skill::getId)
                .distinct()
                .toList();

        addUserSkillGuarantee(offeredSkillIds, recommendation);

        Optional<Recommendation> updatedRecommendation = recommendationRepository.findById(recommendationDto.id());

        if (updatedRecommendation.isEmpty()) {
            throw new ServiceException("Recommendation not found");
        }

        addSkillOffersToRecommendation(offeredSkillIds, updatedRecommendation.get());

        return recommendationMapper.toDto(updatedRecommendation.get());
    }

    @Transactional
    public void delete(Long recommendationId) {
        recommendationRepository.deleteById(recommendationId);
    }

    @Transactional
    public List<RecommendationDto> getAllUserRecommendations(Long receiverId) {
        Page<Recommendation> recommendations = recommendationRepository.findAllByReceiverId(receiverId, Pageable.unpaged());

        return recommendations.get().map(recommendationMapper::toDto).toList();
    }

    @Transactional
    public List<RecommendationDto> getAllGivenRecommendations(Long authorId) {
        Page<Recommendation> recommendations = recommendationRepository.findAllByAuthorId(authorId, Pageable.unpaged());

        return recommendations.get().map(recommendationMapper::toDto).toList();
    }

    /**
     * Сохранить предложенные в рекомендации скиллы.
     * Если у пользователя, которому дают рекомендацию, такой скилл уже есть,
     * то добавить автора рекомендации гарантом к скиллу, который он предлагает,
     * если этот автор еще не стоит там гарантом.
     */
    private void addUserSkillGuarantee(List<Long> offeredSkillIds, Recommendation recommendation) {
        if (offeredSkillIds.isEmpty()) {
            return;
        }

        long receiverId = recommendation.getReceiver().getId();
        long authorId = recommendation.getAuthor().getId();

        List<Long> allReceiverSkillIds = skillRepository.findAllByUserId(receiverId).stream()
                .map(Skill::getId)
                .toList();
        List<Long> existedReceiverSkillsIds = offeredSkillIds.stream()
                .filter(allReceiverSkillIds::contains)
                .toList();

        List<Long> receiverGuaranteeSkillIds = userSkillGuaranteeRepository.findAllByUserId(receiverId).stream()
                .filter(userSkillGuarantee -> userSkillGuarantee.getGuarantor().getId() == authorId)
                .map(UserSkillGuarantee::getSkill)
                .map(Skill::getId)
                .toList();

        List<Long> skillIdsToGuarantee = existedReceiverSkillsIds.stream()
                .filter(skillId -> !receiverGuaranteeSkillIds.contains(skillId))
                .toList();

        skillIdsToGuarantee.forEach(skillId -> {
            userSkillGuaranteeRepository.create(receiverId, skillId, authorId);
        });
    }

    private void addSkillOffersToRecommendation(List<Long> offeredSkillIds, Recommendation recommendation) {
        if (offeredSkillIds.isEmpty()) {
            return;
        }

        List<Long> createdSkillOffersIds = offeredSkillIds.stream()
                .map(skillId -> skillOfferRepository.create(skillId, recommendation.getId()))
                .toList();

        Iterable<SkillOffer> skillOffers = skillOfferRepository.findAllById(createdSkillOffersIds);

        skillOffers.forEach(recommendation::addSkillOffer);
    }
}
