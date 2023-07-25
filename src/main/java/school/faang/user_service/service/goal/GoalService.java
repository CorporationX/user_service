package school.faang.user_service.service.goal;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.util.Message;
import java.text.MessageFormat;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GoalService {
    private static final int MAX_ACTIVE_GOALS = 3;
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;

    @Transactional
    public GoalDto createGoal(GoalDto goal, Long userId){
        int currentUserGoalNum = goalRepository.countActiveGoalsPerUser(userId);
        boolean allSkillsExist = goal.getSkills().stream()
                .allMatch(skill -> skillRepository.findByTitle(skill.toLowerCase()).isPresent());

        if (!allSkillsExist){
            throw new IllegalArgumentException(Message.UNEXISTING_SKILLS);
        }
        if (currentUserGoalNum > MAX_ACTIVE_GOALS){
            throw new IllegalArgumentException(Message.TOO_MANY_GOALS);
        }

        goalRepository.save(goalMapper.goalToEntity(goal));

        return goal;
    }

    @Transactional
    public GoalDto updateGoal(GoalDto goalDto, Long userId) {
        return goalRepository.findById(goalDto.getId())
                .stream()
                .peek(existingGoal -> {
                    existingGoal.setTitle(goalDto.getTitle());
                    existingGoal.setUpdatedAt(LocalDateTime.now());
                    goalRepository.save(existingGoal);
                })
                .map(goalMapper::goalToDto)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(MessageFormat.format("Goal {0} not found", goalDto.getId())));

    }
}