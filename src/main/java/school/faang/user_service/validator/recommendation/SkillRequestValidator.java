package school.faang.user_service.validator.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.skill.SkillRequestNotValidException;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class SkillRequestValidator {
    private final SkillRequestRepository skillRequestRepository;

    public void validateSkillsExist(List<SkillRequest> skillRequests) {
        for (SkillRequest skillRequest : skillRequests) {
            if (skillRequestRepository.findById(skillRequest.getId()).isEmpty()) {
                log.error("Skill not in DB!");
                throw new SkillRequestNotValidException("Skills don't exist in DB!");
            }
        }
    }
}
