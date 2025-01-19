package school.faang.user_service.service.skills;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    public void assignSkillsToUser(Long skillId, Long userId) {
        skillRepository.assignSkillToUser(skillId, userId);
    }

    public boolean skillsExist(List<Long> ids) {
        int existing = skillRepository.countExisting(ids);
        return existing == ids.size();
    }

    public List<Skill> findByIds(List<Long> ids) {
        return skillRepository.findAllById(ids);
    }
}
