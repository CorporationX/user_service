package school.faang.user_service.utils.validationUtils;

import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;
import java.util.List;

public class RecommendationValidation {
    private static final String CONTENT_NULL_EXCEPTION = "Содержание рекомендации не может быть null";
    private static final String CONTENT_EMPTY_EXCEPTION = "Содержание рекомендации не может быть пустым";
    private static final String DATE_EXCEPTION = "Невозможно дать рекомендацию этому пользователю, поскольку " +
            "последняя рекомендация была предложена менее 6 месяцев назад.";
    private static final String DATE_NULL_EXCEPTION = "Дата рекомендации не может быть null";
    private static final String RECOMMENDATION_NULL_EXCEPTION = "Рекомендация не может быть null";
    private static final String SKILL_OFFER_VALID_EXCEPTION = "Предложение о приобретении навыка с идентификатором: " +
            "%d недействительно.";
    private static final int MOUNT_COUNT_LIMIT = 6;

    public static void validateRecommendationContent(String content) {
        if (content == null) {
            throw new DataValidationException(CONTENT_NULL_EXCEPTION);
        } else if (content.isBlank()) {
            throw new DataValidationException(CONTENT_EMPTY_EXCEPTION);
        }
    }

    public static void validateRecommendationDate(LocalDateTime lastRecommendation) {
        if (lastRecommendation == null) {
            throw new DataValidationException(DATE_NULL_EXCEPTION);
        } else if (lastRecommendation.isAfter(LocalDateTime.now().minusMonths(MOUNT_COUNT_LIMIT))) {
            throw new DataValidationException(DATE_EXCEPTION);
        }
    }

    public static void validateSkills(RecommendationDto recommendationDto, List<SkillOffer> allSkillOffers) {
        if (recommendationDto == null) {
            throw new DataValidationException(RECOMMENDATION_NULL_EXCEPTION);
        }
        List<SkillOfferDto> skillOfferDtoList = recommendationDto.getSkillOffersDto();
        List<Long> allSkillOffersIds = allSkillOffers.stream()
                .map(SkillOffer::getId)
                .toList();

        for (SkillOfferDto skillOfferDto : skillOfferDtoList) {
            if (!allSkillOffersIds.contains(skillOfferDto.getId())) {
                throw new DataValidationException(String.format(SKILL_OFFER_VALID_EXCEPTION,
                        skillOfferDto.getId()));
            }
        }
    }
}