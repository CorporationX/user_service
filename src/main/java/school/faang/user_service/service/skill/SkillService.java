package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.SkillRepository;

@Component
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;

    public boolean isExistingSkill(Long skillId) {
        return skillRepository.existsById(skillId);
    }
}
