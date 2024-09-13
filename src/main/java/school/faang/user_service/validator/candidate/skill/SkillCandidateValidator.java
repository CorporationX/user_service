package school.faang.user_service.validator.candidate.skill;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillCandidateValidator {
    public void validateSkillOfferSize(List<SkillOffer> skillOfferList) throws DataValidationException {
        final int requiredSize = 3;
        if (skillOfferList.size() < requiredSize) {
            throw new DataValidationException("Not enough skill offers");
        }
    }
}