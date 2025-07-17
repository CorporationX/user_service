package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalCreateDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalIdDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.GoalService;
import school.faang.user_service.util.Helper;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final GoalValidationService validationService;
    private final Helper helper;

    @Override
    public Goal getGoalById(Long goalId) {
        return goalRepository.getReferenceById(goalId);
    }

    @Override
    public GoalIdDto createGoal(GoalCreateDto goalCreateRq) throws BusinessException {
        validationService.getRequestValidation().createGoal(goalCreateRq);

        return helper.executeInTransaction(status -> {
            Goal mainGoal = createGoalInner(
                    goalCreateRq.getUserId(),
                    goalMapper.toEntity(goalCreateRq.getGoal(), this),
                    goalCreateRq.getGoal(),
                    new Goal(),
                    "goal"
            );

            for (int i = 0; i < goalCreateRq.getGoal().getSubGoals().size(); i++) {
                GoalDto subGoalRq = goalCreateRq.getGoal().getSubGoals().get(i);
                createGoalInner(
                        goalCreateRq.getUserId(),
                        goalMapper.toEntity(subGoalRq, this),
                        subGoalRq,
                        mainGoal,
                        "goal.subGoals[" + i + "]"
                );
            }

            return new GoalIdDto(mainGoal.getId());
        });
    }

    private Goal createGoalInner(Long userId, Goal goal, GoalDto goalRq, Goal parentGoal, String path) throws BusinessException {
        validationService.createGoal(userId, goal, goalRq, parentGoal, path);

        Long mentorId = goal.getMentor().getId();
        if (mentorId == null && parentGoal.getMentor() != null) {
            mentorId = parentGoal.getMentor().getId();
        }

        Goal createdGoal = goalRepository.create(
                goal.getTitle(),
                goal.getDescription(),
                parentGoal.getId(),
                goal.getDeadline() != null ? goal.getDeadline() : parentGoal.getDeadline(),
                mentorId
        );

        if (userId != null && parentGoal.getId() == null) {
            goalRepository.assignGoalToUser(createdGoal.getId(), userId);
        }

        goal.getSkillsToAchieve().stream()
                .map(Skill::getId)
                .forEach(goalSkillId -> goalRepository.addSkillToGoal(goalSkillId, createdGoal.getId()));

        return createdGoal;
    }

    @Override
    public GoalIdDto deleteGoal(Long goalId) {
        validationService.getRequestValidation().deleteGoal(goalId);

        // Приглашения и подцели не удаляются автоматически через Goal Entity, тк не привязаны к ней
        // Удаление скилов и целей пользователя добавлены для единообразия
        return helper.executeInTransaction(status -> {
            goalRepository.findByParent(goalId)
                    .mapToLong(Goal::getId)
                    .forEach(this::deleteGoal);
            goalRepository.deleteGoalSkills(goalId);
            goalRepository.deleteGoalInvitations(goalId);
            goalRepository.deleteGoalFromUser(goalId);
            goalRepository.deleteById(goalId);

            return new GoalIdDto(goalId);
        });
    }

    @Override
    public GoalIdDto updateGoal(Long goalId, GoalDto goalUpdateRq) {
        validationService.getRequestValidation().updateGoal(goalId, goalUpdateRq);

        return helper.executeInTransaction(status -> {
            Goal storedGoal = getGoalById(goalId);
            Goal newGoal = goalMapper.toEntity(goalUpdateRq, this);

            newGoal.setTitle(newGoal.getTitle().isEmpty() ? storedGoal.getTitle() : null);
            newGoal.setDescription(newGoal.getDescription().isEmpty() ? storedGoal.getTitle() : null);

            validationService.updateGoal(goalId, storedGoal, goalUpdateRq);

            updateGoalSkills(storedGoal, goalUpdateRq);
            updateSubGoals(storedGoal, goalUpdateRq);

            Goal updatedGoal = goalRepository.updateGoal(
                    goalId,
                    newGoal.getTitle(),
                    newGoal.getDescription(),
                    newGoal.getDeadline(),
                    newGoal.getMentor().getId()
            );

            return new GoalIdDto(updatedGoal.getId());
        });
    }

    private void updateGoalSkills(Goal goal, GoalDto goalUpdateRq) {
        if (goalUpdateRq.getSkillIds() != null) {
            goalRepository.deleteGoalSkills(goal.getId());
            goalUpdateRq.getSkillIds().forEach(skillId -> goalRepository.addSkillToGoal(skillId, goal.getId()));
        }
    }

    private void updateSubGoals(Goal goal, GoalDto goalUpdateRq) {
        if (goalUpdateRq.getSubGoals() != null) {
            goalRepository.findByParent(goal.getId())
                    .mapToLong(Goal::getId)
                    .forEach(this::deleteGoal);

            for (int i = 0; i < goalUpdateRq.getSubGoals().size(); i++) {
                GoalDto subGoalRq = goalUpdateRq.getSubGoals().get(i);
                createGoalInner(
                        null,
                        goalMapper.toEntity(subGoalRq, this),
                        subGoalRq,
                        goal,
                        "subGoals[" + i + "]"
                );
            }
        }
    }
}
