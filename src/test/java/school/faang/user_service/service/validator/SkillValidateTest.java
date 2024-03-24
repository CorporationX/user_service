package school.faang.user_service.service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validation.skill.SkillValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillValidateTest {

    @InjectMocks
    private SkillValidator skillValidator;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Test
    void testCreateExistingTitle() {
        SkillDto skillDto = prepareData("test");
        when(skillRepository.existsByTitle(skillDto.getTitle()))
                .thenReturn(true);

        assertThrows(DataValidationException.class, () -> skillValidator.validateSkill(skillDto));
    }

    @Test
    void testExceptionOfSkill() {
        Long skillId = 1L;
        Long userId = 1L;

        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class, () -> skillValidator.validateSupplyQuantityCheck(skillId, userId));
    }

    @Test
    void testExceptionNotEnoughOffers() {
        Long skillId = 3L;
        Long userId = 3L;
        List<SkillOffer> skillOffers = List.of(
                skillOfferData(1l, skillData(1L, "test"), recommendationData(1L)),
                skillOfferData(1l, skillData(1L, "test"), recommendationData(2L))
        );

        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(skillOffers);
        assertThrows(DataValidationException.class, () -> skillValidator.validateSupplyQuantityCheck(skillId, userId));
    }

    private SkillDto prepareData(String title) {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle(title);
        return skillDto;
    }

    private Skill skillData(Long id, String title) {
        return Skill
                .builder()
                .id(id)
                .title(title)
                .build();
    }

    private SkillOffer skillOfferData(Long id, Skill skill, Recommendation recommendation) {
        return SkillOffer
                .builder()
                .id(id)
                .skill(skill)
                .recommendation(recommendation)
                .build();
    }

    private Recommendation recommendationData(Long id) {
        return Recommendation
                .builder()
                .id(id)
                .build();
    }
}