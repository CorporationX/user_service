package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.candidate.skill.SkillCandidateValidator;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
public class SkillCandiateValidatorTest {
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @InjectMocks
    private SkillCandidateValidator skillCandidateValidator;

    @Test
    public void testValidateSkillSizeToSmall() {
        long skillId = 1L;
        long userId = 2L;
        List<SkillOffer> skillOffers = List.of(new SkillOffer(), new SkillOffer());
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(skillOffers);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            skillCandidateValidator.validateSkillOfferSize(skillId, userId);
        });
    }

    @Test
    public void testValidationSuccesfull() {
        long skillId = 1L;
        long userId = 2L;
        List<SkillOffer> skillOffers = List.of(new SkillOffer(), new SkillOffer(), new SkillOffer(), new SkillOffer());
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(skillOffers);

        skillCandidateValidator.validateSkillOfferSize(skillId, userId);

        verify(skillRepository, times(1)).assignSkillToUser(skillId, userId);
        for (SkillOffer skillOffer : skillOffers) {
            verify(skillRepository, times(4)).assignGuarantorToUserSkill(userId, skillId, skillOffer.getId());
        }
    }
}

