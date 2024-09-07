package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.dto.RecommendationDto;
import school.faang.user_service.entity.recommendation.dto.SkillOfferDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillInDbValidator {

    private final SkillRepository skillRepository;

    public List<Skill> getSkillsFromDb(RecommendationDto recommendation) {
        List<SkillOfferDto> skillOfferDtos = recommendation.getSkillOffers();

        List<Skill> skills = new ArrayList<>();
        for (SkillOfferDto skillOfferDto : skillOfferDtos) {
            Skill skillOffer = skillRepository.findById(skillOfferDto.getSkillId())
                    .orElseThrow(() -> new DataValidationException("Такого скилла нет в БД"));

            skills.add(skillOffer);
        }

        return skills;
    }
}
