package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exeption.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillValidator {

    private final SkillRepository skillRepository;

    public void validateSkillOffersDto(RecommendationDto recommendationDto) {
        if (recommendationDto.getSkillOffers() == null || recommendationDto.getSkillOffers().isEmpty()) {
            return;
        }
        List<Long> recommendationSkills = recommendationDto.getSkillOffers().stream().
                map(skillOfferDto -> skillOfferDto.getSkill().getId())
                .distinct()
                .toList();

        List<Long> skills = new ArrayList<>();
        skillRepository.findAll().forEach(skill -> skills.add(skill.getId()));

        if (!recommendationSkills.containsAll(skills)) {
            throw new DataValidationException("One or more suggested skills do not exist in the system.");
        }
    }
}
