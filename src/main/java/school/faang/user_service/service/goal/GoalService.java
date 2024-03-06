package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.EntityFieldsException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final SkillRepository skillRepository;
    private final static int MAX_COUNT_GOALS = 3;

    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        validateGoal(userId, goalDto);
        checkSkillsOnExistence(goalDto);
        Goal createdGoal = goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());
        createdGoal.setSkillsToAchieve(skillIdsToSkills(goalDto.getSkillIds()));
        return goalMapper.toDto(goalRepository.save(createdGoal));
    }

    private void validateGoal(Long userId, GoalDto goalDto) {
        checkMaximumGoalsPerUser(userId);
        checkExistingTitleGoal(userId, goalDto);
    }

    private void validateTitle(String title) {
        if (title == null) {
            throw new EntityFieldsException("Goal title can't be null");
        }
        if (title.isEmpty()) {
            throw new EntityFieldsException("Goal title can't be empty");
        }
    }

    private void checkSkillsOnExistence(GoalDto goalDto) {
        List<Long> existingSkills = getIdSkillFromRepository();
        List<Long> idSkillsGoal = goalDto.getSkillIds();
        existingSkills.forEach(skill -> {
            if (!idSkillsGoal.contains(skill)) {
                throw new DataValidationException("Скилла нету в списке доступных скиллов");
            }
        });
    }

    private void checkMaximumGoalsPerUser(Long userId) {
        if (goalRepository.countActiveGoalsPerUser(userId) == MAX_COUNT_GOALS) { // Можно создать свой Exception
            throw new DataValidationException("Достигнуто максимальное количество целей у пользователя");
        }
    }

    private void checkExistingTitleGoal(Long userId, GoalDto goalDto) {
        validateTitle(goalDto.getTitle());
        goalRepository.findGoalsByUserId(userId).forEach(goalFromData -> {
            if (goalDto.getTitle().equalsIgnoreCase(goalFromData.getTitle())) {
                throw new DataValidationException("Название такой цели уже есть");
            }
        });
    }

    private List<Long> getIdSkillFromRepository() {
        return skillRepository.findAll().stream()
                .map(Skill::getId).toList();
    }

    private List<Skill> skillIdsToSkills(List<Long> skillIds) {
        return skillRepository.findAllById(skillIds);
    }
}
