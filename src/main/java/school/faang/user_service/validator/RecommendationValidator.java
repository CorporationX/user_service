package school.faang.user_service.validator;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.service.RecommendationService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationValidator {
    @Value("${recommendation.service.recommendation_period}")
    private int recommendationPeriodInMonths;

    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;


    public void validateBeforeAction(RecommendationDto recommendationDto) {
        validateLastUpdate(recommendationDto);
        validateSkillRepository(recommendationDto);
    }

    public void validateById(long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("The user doesn't exist in the system. ID : " + id);
        }
    }

    public void validateLastUpdate(RecommendationDto recommendationDto) {
        long authorId = recommendationDto.getAuthorId();
        long receiverId = recommendationDto.getReceiverId();

        Optional<Recommendation> lastRecommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId);

        if (lastRecommendation.isPresent()) {
            LocalDateTime lastUpdate = lastRecommendation.get().getUpdatedAt();
            LocalDateTime nowDate = LocalDateTime.now();

            if (ChronoUnit.MONTHS.between(nowDate, lastUpdate) <= recommendationPeriodInMonths) {
                String errorMessage = String.format("The author (ID : %d) cannot give a recommendation to a user (ID : %d)because it hasn't been %d months or more."
                        , authorId, receiverId, recommendationPeriodInMonths);
                throw new DataValidationException(errorMessage);
            }
        }
    }

    private void validateSkillRepository(RecommendationDto recommendationDto) {
        List<SkillOfferDto> skillOffers = recommendationDto.getSkillOffers();

        List<Long> uniqueSkillOfferIds = skillOffers.stream()
                .map(SkillOfferDto::getSkillId).distinct().toList();

        for (var skillOfferId : uniqueSkillOfferIds) {
            if (!skillRepository.existsById(skillOfferId)) {
                String errorMessage =
                        String.format("The skill with ID : %d is doesn't exist in the system", skillOfferId);
                throw new DataValidationException(errorMessage);
            }
        }
    }

    public void validateOfferSkillsIsExisting(List<SkillOfferDto> skillOfferDtos) {
        for (SkillOfferDto skillOfferDto : skillOfferDtos) {
            if (!skillRepository.existsById(skillOfferDto.getSkillId())) {
                String errorMessage =
                        String.format("The offerd skill with ID : %d is doesn't exist in the system"
                                , skillOfferDto.getSkillId());
                throw new EntityNotFoundException(errorMessage);
            }
        }
    }

    public void recommendationExist(long recommendationID) {
        if (!recommendationRepository.existsById(recommendationID)) {
            throw new EntityNotFoundException("Recommendation with ID = " + recommendationID + "doesn't exist in the System");
        }
    }
}
