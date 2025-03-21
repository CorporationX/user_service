package school.faang.user_service.utils.validationUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class RecommendationValidation {
    public static final String CONTENT_NULL_EXCEPTION = "The content of the recommendation cannot be null";
    public static final String CONTENT_EMPTY_EXCEPTION = "The content of the recommendation cannot be empty";
    public static final String DATE_EXCEPTION = "It is not possible to give a recommendation to this user because" +
            " the last recommendation was offered less than 6 months ago.";
    public static final String DATE_NULL_EXCEPTION = "The date of the recommendation cannot be null";
    public static final String RECOMMENDATION_NULL_EXCEPTION = "The recommendation cannot be null";
    public static final String SKILL_OFFER_VALID_EXCEPTION = "There is no such skill in the system";
    public static final String AUTHOR_ID_NULL = "Id of author can't be null";
    public static final String RECEIVER_ID_NULL = "Id of receiver can't be null";
    private static final int MOUNT_COUNT_LIMIT = 6;

    public static void validateRecommendationDtoOnNull(RecommendationDto recommendationDto) {
        if (recommendationDto == null) {
            log.error(RECOMMENDATION_NULL_EXCEPTION);
            throw new DataValidationException(RECOMMENDATION_NULL_EXCEPTION);
        }
    }

    public static void validateAuthorIdAndReceiverId(RecommendationDto recommendationDto) {
        if (recommendationDto.getAuthorId() == null) {
            log.error(AUTHOR_ID_NULL);
            throw new DataValidationException(AUTHOR_ID_NULL);
        } else if (recommendationDto.getReceiverId() == null) {
            log.error(RECEIVER_ID_NULL);
            throw new DataValidationException(RECEIVER_ID_NULL);
        }
    }

    public static void validateRecommendationContent(String content) {
        if (content == null) {
            log.error(CONTENT_NULL_EXCEPTION);
            throw new DataValidationException(CONTENT_NULL_EXCEPTION);
        } else if (content.isBlank()) {
            log.error(CONTENT_EMPTY_EXCEPTION);
            throw new DataValidationException(CONTENT_EMPTY_EXCEPTION);
        }
    }

    public static void validateRecommendationDate(LocalDateTime lastRecommendation) {
        if (lastRecommendation == null) {
            log.error(DATE_NULL_EXCEPTION);
            throw new DataValidationException(DATE_NULL_EXCEPTION);
        } else if (lastRecommendation.isAfter(LocalDateTime.now().minusMonths(MOUNT_COUNT_LIMIT))) {
            log.error(DATE_EXCEPTION);
            throw new DataValidationException(DATE_EXCEPTION);
        }
    }

    public static void validateSkills(RecommendationDto recommendationDto, List<SkillOfferDto> allSkillOffers) {
        List<SkillOfferDto> skillOfferDtoList = recommendationDto.getSkillOffersDto();
        List<Long> allSkillIds = allSkillOffers.stream()
                .map(SkillOfferDto::getSkillId)
                .toList();

        for (SkillOfferDto skillOfferDto : skillOfferDtoList) {
            if (!allSkillIds.contains(skillOfferDto.getSkillId())) {
                log.error(SKILL_OFFER_VALID_EXCEPTION);
                throw new DataValidationException(SKILL_OFFER_VALID_EXCEPTION);
            }
        }
    }


}