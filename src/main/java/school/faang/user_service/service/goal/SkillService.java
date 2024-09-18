package school.faang.user_service.service.goal;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    public boolean existsById(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        } else {
            return ids.stream().allMatch(skillRepository::existsById);
        }
    }

    @Transactional
    public void addSkillsToUserId(List<Long> skillIds, Long userId) {
        validateExistById(skillIds);

        skillIds.stream()
                .map(skillRepository::findById)
                .flatMap(Optional::stream)
                .forEach(skill -> skillRepository.assignSkillToUser(skill.getId(), userId));
    }

    @Transactional
    public void removeSkillsFromUserId(List<Long> skillIds, Long userId) {
        validateExistById(skillIds);

        skillIds.stream()
                .map(skillRepository::findById)
                .flatMap(Optional::stream)
                .forEach(skill -> skillRepository.removeSkillFromUserId(skill.getId(), userId));
    }

    @Transactional
    public void addSkillsToGoal(List<Long> skillIds, Goal goal) {
        validateExistById(skillIds);

        List<Skill> skills = skillIds.stream()
                .map(skillRepository::findById)
                .flatMap(Optional::stream)
                .toList();

        skills.forEach(skill -> skillRepository.addSkillToGoalId(skill.getId(), goal.getId()));

        goal.getSkillsToAchieve().addAll(skills);
    }

    @Transactional
    public void removeSkillsFromGoal(List<Long> skillIds, Goal goal) {
        validateExistById(skillIds);

        List<Skill> skills = skillIds.stream()
                .map(skillRepository::findById)
                .flatMap(Optional::stream)
                .toList();

        skills.forEach(skill -> skillRepository.removeSkillFromGoalId(skill.getId(), goal.getId()));

        goal.getSkillsToAchieve().removeAll(skills);
    }

    private void validateExistById(final List<Long> ids) {
        if (ids != null && !ids.isEmpty() && !existsById(ids)) {
            throw new DataValidationException("There is no skill with this name");
        }
    }
}