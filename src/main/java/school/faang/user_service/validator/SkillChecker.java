package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class SkillChecker {

    private final SkillRepository skillRepository;

    public void check(List<SkillOfferDto> skills) {
        if (skills == null || skills.isEmpty()) {
            return;
        }

        List<Long> skillIds = skills.stream()
                .map(SkillOfferDto::getSkillId)
                .distinct()
                .collect(Collectors.toList());

        if (skills.size() != skillIds.size()) {
            throw new DataValidationException("List of skills contains not unique skills!");
        }

//        if (!skillIds.isEmpty()) {
        if (skillIds.size() != skillRepository.countExisting(skillIds)) {
            throw new DataValidationException("List of skills contains not valid skills!");
        }
    }
}


