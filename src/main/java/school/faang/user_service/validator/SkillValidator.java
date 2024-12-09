package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.skill.SkillRepository;
import school.faang.user_service.repository.SkillOfferRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillValidator {
    private final static int MIN_SKILL_OFFERS = 3;

    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;

    public void validateExistTitle(String title) {
        if (skillRepository.existsByTitle(title)) {
            throw new DataValidationException(title);
        }
    }

    public void validateUserSkillExist(long skillId, long userId) {
        if (!skillRepository.findUserSkill(skillId, userId).isPresent()) {
            throw new DataValidationException("У пользователя уже есть этот скилл");
        }
    }

    public void validateSkillOfferCount(long skillId, long userId) {
        List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);

        if (offers.size() < MIN_SKILL_OFFERS) {
            throw new DataValidationException("Недостаточно предложений для получения скилла");
        }
    }
}
