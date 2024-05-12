package school.faang.user_service.validation.skill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationExeption;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor

public class SkillValidator {
    private static final int MIN_SKILL_OFFERS = 3;
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;

    public void validateSkill(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationExeption("Скилл с названием " + skillDto.getTitle() + " уже существует.");
        }
    }

    public void validateSkillTitle(SkillDto skillDto) {
        if (skillDto.getTitle().isBlank()) {
            throw new DataValidationExeption("Название скилла не может быть пустым.");
        }
    }

    public void validateSkillPresent(long skillId, long userId) {
        Optional<Skill> userSkill = skillRepository.findUserSkill(skillId, userId);
        if (userSkill.isPresent()) {
            throw new DataValidationExeption("User " + userId + " already has skill with ID: " + skillId);
        }
    }

    public void validateMinSkillOffers(int countOffersSkill, long skillId, long userId) {
        if (countOffersSkill < MIN_SKILL_OFFERS) {
            throw new DataValidationExeption("Skill with ID: " + skillId + " hasn't enough offers for user with ID: " + userId);
        }
    }
}
