package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.validator.candidate.skill.SkillCandidateValidator;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class SkillCandidateValidatorTest {
    @InjectMocks
    private SkillCandidateValidator skillCandidateValidator;

    @Test
    public void testValidateSkillSizeToSmall() {
        List<SkillOffer> skillOfferList = List.of(new SkillOffer(), new SkillOffer());

        Assertions.assertThrows(DataValidationException.class, () -> {
            skillCandidateValidator.validateSkillOfferSize(skillOfferList);
        });
    }

    @Test
    public void testValidationSuccess() {
        List<SkillOffer> skillOffers = List.of(new SkillOffer(), new SkillOffer(), new SkillOffer(), new SkillOffer());

        Assertions.assertDoesNotThrow(() -> {
            skillCandidateValidator.validateSkillOfferSize(skillOffers);
        });
    }
}


