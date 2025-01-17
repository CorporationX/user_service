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
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillRepository skillRepository;
    private final int numberOfMonthAfterCreatedRecommendation = 6;

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
                    var message = String.format("Recommendation with this id "
                            + "are not relevant: %s", recommendation.getId());
                    log.error(message);
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
                    var message = String.format("The recommendation with this id "
                            + "was not found: %s", id);
                    log.error(message);
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
        if (recommendation.getContent() == null || recommendation.getContent().isBlank()) {
            var message = "The recommendation should have a content";
            log.error(message);
            throw new DataValidationException(message);
        }

        recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        recommendation.getId(), recommendation.getReceiver().getId())
                .ifPresent(pastRecommendation -> {
                    boolean isLaterThanPeriod = pastRecommendation.getCreatedAt()
                            .plusMonths(numberOfMonthAfterCreatedRecommendation)
                            .isAfter(LocalDateTime.now());
                    if (!isLaterThanPeriod) {
                        var message = String.format("The period for issuing "
                                        + "recommendations should be %s months",
                                numberOfMonthAfterCreatedRecommendation);
                        log.error(message);
                        throw new DataValidationException(message);
                    }
                });

        List<Skill> allSkills = skillRepository.findAll()
                .stream()
                .toList();

        recommendation.getSkillOffers().forEach(skillOffer -> {
            if (!allSkills.contains(skillOffer.getSkill())) {
                var message = String.format("There is no Skill with"
                        + "this id in the DB: %s", skillOffer.getId());
                log.error(message);
                throw new DataValidationException(message);
            }
        });
    }
}