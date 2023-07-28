package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
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

    public GoalDto createGoal(long userId, GoalDto goalDto) {
        validator.creatingGoalServiceValidation(userId, goalDto);
        goalDto.setUserIds(List.of(userId));
        //goalDto.getSkillIds().forEach(id -> goalRepository.addSkillToGoal(id, goal.getId())); По заданию этот метод должен быть и мне его делать не нужно
        goalRepository.save(goalMapper.toEntity(goalDto));
        return goalDto;
    }
}
