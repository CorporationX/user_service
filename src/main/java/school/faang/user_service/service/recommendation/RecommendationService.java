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
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.messages.ErrorMessageSource;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillRepository skillRepository;
    private final ErrorMessageSource errorMessageSource;
    private final int monthsAllowedAfterRecommendationCreation = 6;

    @Transactional
    public Recommendation create(Recommendation recommendation) {
        validateRecommendation(recommendation);

        User receiver = recommendation.getReceiver();
        User author = recommendation.getAuthor();

        updateSkillOffers(recommendation, receiver, author);
        Long id = recommendationRepository.create(
                author.getId(),
                receiver.getId(),
                recommendation.getContent()
        );

        return recommendationRepository.findById(id).get();
    }

    @Transactional
    public Recommendation update(Recommendation recommendation) {
        validateRecommendation(recommendation);
        recommendationRepository.findById(recommendation.getId())
                .orElseThrow(() -> {
                    var message = errorMessageSource.getMessage(
                            "error.recommendation.not.found", recommendation.getId());
                    return new DataValidationException(message);
                });

        User receiver = recommendation.getReceiver();
        User author = recommendation.getAuthor();

        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        updateSkillOffers(recommendation, receiver, author);
        recommendationRepository.update(
                author.getId(), receiver.getId(), recommendation.getContent());

        return recommendationRepository.findById(recommendation.getId()).get();
    }


    @Transactional
    public void delete(long id) {
        Recommendation recommendationToDelete = recommendationRepository.findById(id)
                .orElseThrow(() -> {
                    var message = errorMessageSource.getMessage(
                            "error.recommendation.not.found", id);
                    return new DataValidationException(message);
                });

        userSkillGuaranteeRepository.deleteAllByGuarantorId(
                recommendationToDelete.getAuthor().getId());
        skillOfferRepository.deleteAllByRecommendationId(id);
        recommendationRepository.deleteById(id);
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
        List<SkillOffer> skillOffersOfReceiver = skillOfferRepository.findAllByUserId(
                receiver.getId());
        List<UserSkillGuarantee> updateGuarantees = new ArrayList<>();

        recommendation.getSkillOffers().forEach(skillOffer -> {
            if (skillOffersOfReceiver.contains(skillOffer)) {
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
                        recommendation.getId(), recommendation.getReceiver().getId());

        pastRecommendation.ifPresent(rec -> {
            LocalDateTime expirationDate = rec.getCreatedAt()
                    .plusMonths(monthsAllowedAfterRecommendationCreation);
            boolean isWithinAllowedPeriod = expirationDate.isAfter(LocalDateTime.now());

            if (!isWithinAllowedPeriod) {
                var message = errorMessageSource.getMessage(
                        "error.recommendation.period",
                        monthsAllowedAfterRecommendationCreation);
                throw new DataValidationException(message);
            }
        });

        List<Skill> allSkills = skillRepository.findAll()
                .stream()
                .toList();

        recommendation.getSkillOffers().forEach(skillOffer -> {
            if (!allSkills.contains(skillOffer.getSkill())) {
                var message = errorMessageSource.getMessage(
                        "error.skill.not.found", skillOffer.getSkill().getId());
                throw new DataValidationException(message);
            }
        });
    }
}