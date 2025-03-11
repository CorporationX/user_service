package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;

    public boolean existsById(Long skillId) {
        return skillRepository.existsById(skillId);
    }

    public boolean existAllSkills(List<Long> skillIds) {
        for (Long skillId : skillIds) {
            if (!existsById(skillId)) {
                return false;
            }
        }
        return true;
    }
}

