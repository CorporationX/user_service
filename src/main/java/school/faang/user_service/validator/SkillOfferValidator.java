package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillOfferValidator {

    private final SkillRepository skillRepository;

    public void validateSkillsListNotEmptyOrNull(List<SkillOfferDto> skills) {
        if (skills == null || skills.isEmpty()) {
            throw new DataValidationException("You should choose some skills");
        }
    }

    public void validateSkillsAreInRepository(List<SkillOfferDto> skills) {
        List<Long> skillIds = getUniqueSkillIds(skills);
        int countedSkills = skillRepository.countExisting(skillIds);

        if (skills.size() != countedSkills) {
            throw new DataValidationException("Invalid skills");
        }
    }

    private List<Long> getUniqueSkillIds(List<SkillOfferDto> skills) {
        return skills.stream()
                .map(SkillOfferDto::getSkillId)
                .distinct()
                .toList();
    }
}
