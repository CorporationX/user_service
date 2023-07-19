package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.ResponseGoalDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;
    private final int MAX_GOALS_PER_USER = 3;

    @Transactional
    public ResponseGoalDto createGoal(Long userId, CreateGoalDto goalDto) {
        validateGoalToCreate(userId, goalDto);

        return goalMapper.toResponseGoalDtoFromGoal(goalRepository.save(goalMapper.toGoalFromCreateGoalDto(goalDto)));
    }

    private void validateGoalToCreate(Long userId, CreateGoalDto goalDto) {
        if (goalDto == null) {
            throw new IllegalArgumentException("Goal cannot be null");
        }

        if (goalDto.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title cannot be null");
        }

        if (goalRepository.countActiveGoalsPerUser(userId) >= MAX_GOALS_PER_USER) {
            throw new IllegalArgumentException("Maximum number of goals for this user reached");
        }

        List<SkillDto> skillsToAchieve = goalDto.getSkillsToAchieve();
        skillsToAchieve.forEach(skill -> {
            if (!skillRepository.existsByTitle(skill.getTitle())) {
                throw new IllegalArgumentException("Skill not found");
            }
        });
    }
}
