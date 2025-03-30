package school.faang.user_service.service.recommendation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.enums.RatingType;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.rating.annotation.RatingChanging;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static school.faang.user_service.utils.RecommendationErrorMessage.RECOMMENDATION_NOT_FOUND;
import static school.faang.user_service.utils.RecommendationErrorMessage.RECOMMENDATION_PERIOD;
import static school.faang.user_service.utils.RecommendationErrorMessage.SKILL_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillRepository skillRepository;
    private final int monthsAllowedAfterRecommendationCreation = 6;

    @RatingChanging(ratingType = RatingType.RECOMMENDATION_RATING)
    @Transactional
    public Recommendation create(Recommendation recommendation) {
        validateRecommendation(recommendation);

        User receiver = recommendation.getReceiver();
        User author = recommendation.getAuthor();

        Long id = recommendationRepository.create(
                author.getId(),
                receiver.getId(),
                recommendation.getContent()
        );

        recommendation.setId(id);
        updateSkillOffers(recommendation, receiver, author);

        return recommendationRepository.findById(id).get();
    }

    @Transactional
    public Recommendation update(Recommendation recommendation) {
        validateRecommendation(recommendation);
        Long recommendationId = recommendation.getId();
        recommendationRepository.findById(recommendation.getId())
                .orElseThrow(() -> {
                    var message = String.format(RECOMMENDATION_NOT_FOUND,
                            recommendationId);
                    return new DataValidationException(message);
                });

        User receiver = recommendation.getReceiver();
        User author = recommendation.getAuthor();

        skillOfferRepository.deleteAllByRecommendationId(recommendationId);
        updateSkillOffers(recommendation, receiver, author);
        recommendationRepository.update(
                author.getId(), receiver.getId(), recommendation.getContent());

        return recommendationRepository.findById(recommendationId).get();
    }


    @Transactional
    public void delete(long id) {
        Recommendation recommendationToDelete = recommendationRepository.findById(id)
                .orElseThrow(() -> {
                    var message = String.format(RECOMMENDATION_NOT_FOUND, id);
                    return new DataValidationException(message);
                });

        deleteRecommendation(recommendationToDelete);
    }

    @RatingChanging(ratingType = RatingType.RECOMMENDATION_RATING, positiveAction = false)
    private void deleteRecommendation(Recommendation recommendation) {
        userSkillGuaranteeRepository.deleteAllByGuarantorId(
                recommendation.getAuthor().getId());
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        recommendationRepository.deleteById(recommendation.getId());
    }

    public Map<Long, Integer> getNumberOfReceivedRecommendationsPerUser() {
        return recommendationRepository.countReceivedRecommendationsPerUser();
    }

    public Map<Long, Integer> getNumberOfGivenRecommendationsPerUser() {
        return recommendationRepository.countGivenRecommendationsPerUser();
    }

    public List<Recommendation> getAllUserRecommendations(long receiverId) {
        return recommendationRepository.findAllByReceiverId(
                receiverId, Pageable.unpaged()).toList();
    }

    public List<Recommendation> getAllGivenRecommendations(long authorId) {
        return recommendationRepository.findAllByAuthorId(
                authorId, Pageable.unpaged()).toList();
    }

    private void updateSkillOffers(Recommendation recommendation, User receiver, User author) {
        List<Long> skillOfReceiverIds = skillRepository.findAllByUserId(
                        receiver.getId()).stream()
                .map(Skill::getId)
                .toList();
        List<UserSkillGuarantee> updateGuarantees = new ArrayList<>();

        recommendation.getSkillOffers().forEach(skillOffer -> {
            if (skillOfReceiverIds.contains(skillOffer.getSkill().getId())) {
                updateGuarantees.add(UserSkillGuarantee.builder()
                        .skill(skillOffer.getSkill())
                        .user(receiver)
                        .guarantor(author)
                        .build()
                );
            } else {
                skillOfferRepository.create(
                        skillOffer.getSkill().getId(),
                        recommendation.getId()
                );
            }
        });

        if (!updateGuarantees.isEmpty()) {
            updateGuarantees.removeAll(
                    userSkillGuaranteeRepository.findAllByUserId(receiver.getId()));
            updateGuarantees.forEach(userSkillGuaranteeRepository::save);
        }
    }

    private void validateRecommendation(Recommendation recommendation) {
        Optional<Recommendation> pastRecommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        recommendation.getAuthor().getId(),
                        recommendation.getReceiver().getId());

        pastRecommendation.ifPresent(rec -> {
            LocalDateTime expirationDate = rec.getCreatedAt()
                    .plusMonths(monthsAllowedAfterRecommendationCreation);
            boolean isWithinAllowedPeriod = expirationDate.isBefore(LocalDateTime.now());

            if (!isWithinAllowedPeriod) {
                var message = String.format(RECOMMENDATION_PERIOD,
                        monthsAllowedAfterRecommendationCreation);
                throw new DataValidationException(message);
            }
        });

        List<Long> allSkills = skillRepository.findAll().stream()
                .map(Skill::getId)
                .toList();

        recommendation.getSkillOffers().forEach(skillOffer -> {
            if (!allSkills.contains(skillOffer.getSkill().getId())) {
                var message = String.format(SKILL_NOT_FOUND,
                        skillOffer.getSkill().getId());
                throw new DataValidationException(message);
            }
        });
    }
}