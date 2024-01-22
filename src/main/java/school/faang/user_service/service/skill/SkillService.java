package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.SkillRepository;


@Component
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    public boolean existsById(long id) {
        return skillRepository.existsById(id);
    }

    public Skill findById(long id) {
        return skillRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Навык не найден"));
    }
}
