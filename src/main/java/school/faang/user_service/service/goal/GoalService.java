package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.goal.GoalValidator;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final GoalMapper goalMapper;
    private final GoalValidator goalValidator;
    private final UserService userService;


    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {

        Goal foundGoal = findById(goalId);

        Goal goal = goalMapper.toEntity(goalDto);
        goal.setId(goalId);


        if (foundGoal.getStatus() == GoalStatus.COMPLETED) {
            throw new DataValidationException("Цель уже завершена");
        }

        goalValidator.validateSkills(goal.getSkillsToAchieve());

        List<Skill> skillsToUpdate = goalDto.getSkillIds().stream().map(skillService::findById).toList();
        goal.setSkillsToAchieve(skillsToUpdate);

        if (goal.getStatus() == GoalStatus.COMPLETED) {
            goal.getUsers().forEach(user -> skillsToUpdate
                    .forEach(skill -> skillService.assignSkillToUser(user.getId(), skill.getId())));
        }


        return goalMapper.toDto(goalRepository.save(goal));
    }


    public Goal findById(long id) {
        return goalRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Цель не найдена"));
    }


    public GoalDto createGoal(long userId, GoalDto goalDto) {

        goalValidator.validate(userId, goalDto);

        Goal goalToSave = goalMapper.toEntity(goalDto);
        if (goalDto.getParentId() != null) {
            Goal parent = findById(goalDto.getParentId());
            goalToSave.setParent(parent);
        }

        if (!goalDto.getSkillIds().isEmpty()) {
            List<Skill> goalSkills = new ArrayList<>();
            goalDto.getSkillIds().forEach(skillId ->
                    goalSkills.add(skillService.findById(skillId)));
            goalToSave.setSkillsToAchieve(goalSkills);
        }

        User userToUpdate = userService.findById(userId);
        userToUpdate.getGoals().add(goalToSave);
        userService.saveUser(userToUpdate);

        return goalMapper.toDto(goalRepository.save(goalToSave));
    }

    public int countActiveGoalsPerUser(long userId) {
        return goalRepository.countActiveGoalsPerUser(userId);
    }


    public void deleteGoal(long goalId) {
        goalRepository.deleteById(goalId);
    }
}
