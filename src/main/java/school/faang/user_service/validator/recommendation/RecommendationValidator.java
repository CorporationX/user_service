package school.faang.user_service.validator.recommendation;

import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

public interface RecommendationValidator {
    void validateLastRecommendationToThisReceiverInterval(User author, User receiver);
    void validaIfSkillsFromOfferNotExist(RecommendationDto recommendationDto);
    void checkIfRecommendationNotExist(long id);
    boolean checkIsGuarantor(Skill skill, long authorId);
    void validateAuthorAndReceiver(RecommendationDto recommendationDto, User author, User receiver);
}
