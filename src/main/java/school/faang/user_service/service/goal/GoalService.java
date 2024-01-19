package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.goal.GoalValidator;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final GoalMapper goalMapper;
    private final UserService userService;
    private final GoalValidator goalValidator;
    private static final int MAX_GOALS_PER_USER = 3;


    public GoalDto createGoal(long userId, GoalDto goalDto) {

        goalValidator.validate(userId, goalDto, MAX_GOALS_PER_USER);

        Goal goalToSave = goalMapper.toEntity(goalDto);
        if (goalDto.getParentId() != null) {
            goalToSave.setParent(findById(goalDto.getParentId()));
        }

        if (goalDto.getSkillIds() != null) {
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

    public Goal findById(long id) {
        return goalRepository.findById(id).orElseThrow(() -> new DataValidationException("Цель не найдена"));
    }
}
