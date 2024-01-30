package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.SkillRepository;


@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;

    public Skill findById(Long skillId) {
        return skillRepository.findById(skillId).orElseThrow(() ->
                new EntityNotFoundException("Skill with id = " + skillId + " is not exists"));
    }
}
