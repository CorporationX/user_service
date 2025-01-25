package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.adapter.SkillRepositoryAdapter;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepositoryAdapter skillRepositoryAdapter;

    public Optional<Skill> findSkillById(Long id) {
        return skillRepositoryAdapter.findById(id);
    }

    public List<Skill> findSkillsByIds(List<Long> ids) {
        return skillRepositoryAdapter.findAllById(ids);
    }

    public List<Skill> findSkillsByUserId(long userId) {
        return skillRepositoryAdapter.findAllByUserId(userId);
    }

    public boolean skillExistsByTitle(String title) {
        return skillRepositoryAdapter.existsByTitle(title);
    }

    public void assignSkillToUser(long skillId, long userId) {
        if (!skillRepositoryAdapter.existsById(skillId)) {
            throw new IllegalArgumentException("Skill with ID " + skillId + " does not exist.");
        }
        skillRepositoryAdapter.assignSkillToUser(skillId, userId);
    }

    public List<Skill> getAllSkills() {
        return skillRepositoryAdapter.findAll();
    }
}