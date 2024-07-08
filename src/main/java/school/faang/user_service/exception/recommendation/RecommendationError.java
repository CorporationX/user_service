package school.faang.user_service.exception.recommendation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RecommendationError {
    RECOMMENDATION_EXPIRATION_TIME_NOT_PASSED("You can only give a recommendation to the same user once every 6 months"),
    ENTITY_IS_NOT_FOUND("Entity is not found"),
    SKILL_IS_NOT_FOUND("Skill could not be found"),
    RECOMMENDATION_IS_NOT_FOUND("Recommendation could not be found");

    private final String message;
}
