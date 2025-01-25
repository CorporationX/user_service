package school.faang.user_service.repository.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SkillRepositoryAdapter {

    private final SkillRepository skillRepository;

    public Optional<Skill> findById(Long id) {
        return skillRepository.findById(id);
    }

    public List<Skill> findAllById(List<Long> ids) {
        return skillRepository.findAllById(ids);
    }

    public List<Skill> findAllByUserId(long userId) {
        return skillRepository.findAllByUserId(userId);
    }

    public boolean existsById(Long id) {
        return skillRepository.existsById(id);
    }

    public boolean existsByTitle(String title) {
        return skillRepository.existsByTitle(title);
    }

    public void assignSkillToUser(long skillId, long userId) {
        skillRepository.assignSkillToUser(skillId, userId);
    }

    public List<Skill> findAll() {
        return skillRepository.findAll();
    }
}