package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;
    private final GoalValidator validator;
    private final UserContext userContext;

    public GoalDto createGoal(GoalDto goalDto) {
        long userId = userContext.getUserId();
        validator.creatingGoalServiceValidation(userId, goalDto);
        goalDto.setUserIds(List.of(userId));
        goalRepository.save(goalMapper.toEntity(goalDto));
        return goalDto;
    }
}
