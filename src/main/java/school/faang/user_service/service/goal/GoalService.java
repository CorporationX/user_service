package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
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
import school.faang.user_service.validator.goal.GoalValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final GoalMapper goalMapper;
    private final GoalValidator goalValidator;


    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {

        Goal foundGoal = findById(goalId);

        Goal goal = goalMapper.toEntity(goalDto);
        goal.setId(goalId);


        if (foundGoal.getStatus() == GoalStatus.COMPLETED) {
            throw new DataValidationException("Цель уже завершена");
        }

        goalValidator.validateSkills(goal.getSkillsToAchieve());

        if (goal.getStatus() == GoalStatus.COMPLETED) {
            goal.getUsers().forEach(user -> foundGoal.getSkillsToAchieve()
                    .forEach(skill -> skillService.assignSkillToUser(user.getId(), skill.getId())));

        }

        List<Skill> skillsToUpdate = goalDto.getSkillIds().stream().map(skillService::findById).toList();
        goal.setSkillsToAchieve(skillsToUpdate);

        return goalMapper.toDto(goalRepository.save(goal));
    }


    public Goal findById(long id) {
        return goalRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Цель не найдена"));
    }
}
