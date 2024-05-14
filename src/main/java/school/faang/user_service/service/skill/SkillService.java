package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.ExceptionMessages;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;

    public Skill getSkillById(Long id) {
        return skillRepository.findById(id).orElseThrow(() -> new DataValidationException(ExceptionMessages.NON_EXIST_SKILLS.getMessage()));
    }

    public boolean checkSkillsInDB(List<Skill> skills) {
        return skills.stream()
                .allMatch(skill -> skillRepository.existsByTitle(skill.getTitle()));
    }

    public int checkAmountSkillsInDB(List<Long> skillsIds) {
        return skillRepository.countExisting(skillsIds);
    }

    public void assignSkillToUser(long skillId, long userId) {
        skillRepository.assignSkillToUser(skillId, userId);
    }
}
