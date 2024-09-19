package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    public List<Skill> getSkillByIds(List<Long> ids) {
        if (ids == null) {
            throw new DataValidationException("List ids cannot be null");
        }

        return skillRepository.findByIdIn(ids);
    }

    public void saveSkill(Skill skill) {
        if (skill == null) {
            throw new DataValidationException("Skill cannot be null");
        }

        skillRepository.save(skill);
    }
}
