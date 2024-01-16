package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;

@Component
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final GoalMapper goalMapper;
    private final UserService userService;
    private final int MAX_GOALS_PER_USER = 3;



    public GoalDto createGoal(long userId, GoalDto goalDto) {
        if (goalRepository.countActiveGoalsPerUser(userId) == MAX_GOALS_PER_USER) {
            throw new DataValidationException("Достигнуто максимальное количество целей");
        }
        if (!goalDto.getSkillIds().stream().allMatch(skillService::validateSkillById)) {
            throw new DataValidationException("Некорректные скиллы");
        }
        Goal goalToSave = goalMapper.toEntity(goalDto);
        if (goalRepository.existsById(goalDto.getParentId())){
            goalToSave.setParent(goalRepository.findById(goalDto.getParentId()).get());
        }
        goalToSave.setSkillsToAchieve(skillService.findSkillsByGoalId(goalDto.getId()));

        User userToUpdate = userService.findUserById(userId).get();
        userToUpdate.getGoals().add(goalToSave);
        userService.saveUser(userToUpdate);

        return goalMapper.toDto(goalRepository.save(goalToSave));
    }
}
