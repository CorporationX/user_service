package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;

    public void validateSkills(List<Long> skillIds) {
        for (Long skillId : skillIds) {
            if (!skillRepository.existsById(skillId)) {
                throw new EntityNotFoundException("Skill with id " + skillId + " not found.");
            }
        }
    }
}
