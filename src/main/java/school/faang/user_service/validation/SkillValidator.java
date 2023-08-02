package school.faang.user_service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SkillValidator {

    private final SkillRepository skillRepository;

    public void validateSkillOffersDto(RecommendationDto recommendationDto) {
        if (recommendationDto.getSkillOffers() == null || recommendationDto.getSkillOffers().isEmpty()) {
            return;
        }
        Set<Long> recommendationSkills = recommendationDto.getSkillOffers().stream().
                map(SkillOfferDto::getSkill)
                .collect(Collectors.toSet());

        Set<Long> skills = new LinkedHashSet<>();
        skillRepository.findAll().forEach(skill -> skills.add(skill.getId()));

        if (!skills.containsAll(recommendationSkills)) {
            throw new DataValidationException("One or more suggested skills do not exist in the system.");
        }
    }
}
