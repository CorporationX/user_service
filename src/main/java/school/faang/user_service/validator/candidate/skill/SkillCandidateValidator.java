package school.faang.user_service.validator.candidate.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillCandidateValidator {
    public void validateSkillOfferSize(List<SkillOffer> skillOfferList) {
        final int requiredSize = 3;
        if (skillOfferList.size() < requiredSize) {
            throw new DataValidationException("Not enough skill offers");
        }
    }
}