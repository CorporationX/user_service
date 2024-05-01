package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.validator.GoalValidator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final GoalMapper goalMapper;
    private final GoalValidator goalValidator;
    private final UserRepository userRepository;

    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        goalValidator.validateBeforeCreate(userId ,goalDto);

        Goal goalToCreate = goalMapper.toEntity(goalDto);
        if (Objects.nonNull(goalDto.getParentId())) {
            goalRepository.findById(goalDto.getParentId())
                    .ifPresent(goalToCreate::setParent);
        }
        List<Skill> skills = goalDto.getSkillIds().stream()
                .map(skillService::getSkillById)
                .toList();
        goalToCreate.setSkillsToAchieve(skills);
        Optional<User> user =  userRepository.findById(userId);
        goalToCreate.getUsers().add(user.get());
        goalToCreate.setStatus(GoalStatus.ACTIVE);

        goalRepository.save(goalToCreate);
        return goalMapper.toDto(goalToCreate);
    }

    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        Goal goalToUpdate = getGoalById(goalId);
        goalValidator.validateBeforeUpdate(goalToUpdate, goalDto);
        updateFields(goalToUpdate, goalDto);

        if (goalDto.getStatus() == GoalStatus.COMPLETED) {
            assignSkillToUser(goalToUpdate);
        }
        goalToUpdate.setStatus(GoalStatus.COMPLETED);

        goalRepository.save(goalToUpdate);
        return goalMapper.toDto(goalToUpdate);
    }

    private void assignSkillToUser(Goal goalToUpdate) {
        List<User> users = goalToUpdate.getUsers();
        users.forEach((user) -> goalToUpdate.getSkillsToAchieve()
                .forEach((skill) -> {
                    if (!user.getSkills().contains(skill)) {
                        skillService.assignSkillToUser(skill.getId(), user.getId());
                    }
                }));
    }

    private void updateFields(Goal goalToUpdate, GoalDto goalDto) {
        if (Objects.nonNull(goalDto.getParentId())) {
            goalRepository.findById(goalDto.getParentId())
                    .ifPresent(goalToUpdate::setParent);
        }

        goalToUpdate.setTitle(goalToUpdate.getTitle());

        if (Objects.nonNull(goalDto.getDescription())) {
            goalToUpdate.setDescription(goalDto.getDescription());
        }

        if (Objects.nonNull(goalDto.getSkillIds())) {
            List<Skill> skills = goalDto.getSkillIds().stream()
                    .map(skillService::getSkillById)
                    .toList();
            goalToUpdate.setSkillsToAchieve(skills);
        }
    }

    public void deleteGoal(long goalId) {
        goalRepository.deleteById(goalId);
    }

    private Goal getGoalById(Long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Цель с id " + goalId + " + не найдена"));
    }
}
