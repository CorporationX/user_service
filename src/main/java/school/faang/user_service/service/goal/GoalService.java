package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final GoalMapper goalMapper;

    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {

        Optional<Goal> foundGoal = goalRepository.findById(goalId);
        if (foundGoal.isEmpty()) {
            throw new DataValidationException("Цель не найдена");
        }

        Goal newGoal = goalMapper.toEntity(goalDto);
        newGoal.setId(goalId);
        Goal oldGoal = foundGoal.get();

        if (oldGoal.getStatus() == GoalStatus.COMPLETED &&
                newGoal.getStatus() == GoalStatus.COMPLETED) {
            throw new DataValidationException("Цель уже завершена");
        }

        if (!newGoal.getSkillsToAchieve().stream()
                .allMatch(skillService::validateSkill)) {
            throw new DataValidationException("Некорректные скиллы");
        }

        if (newGoal.getStatus() == GoalStatus.COMPLETED) {
            newGoal.getUsers().forEach(user -> oldGoal.getSkillsToAchieve()
                    .forEach(skill -> skillService.assignSkillToUser(user.getId(), skill.getId())));

        }

        List<Skill> updatedSkills = goalDto.getSkillIds().stream().map(skillService::findById).toList();
        newGoal.setSkillsToAchieve(updatedSkills);

        return goalMapper.toDto(goalRepository.save(newGoal));
    }
}
