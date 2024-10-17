package school.faang.user_service.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.model.event.GoalCompletedEvent;
import school.faang.user_service.model.dto.GoalDto;
import school.faang.user_service.model.entity.Goal;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.enums.GoalStatus;
import school.faang.user_service.publisher.GoalCompletedEventPublisher;
import school.faang.user_service.repository.GoalRepository;
import school.faang.user_service.service.GoalService;
import school.faang.user_service.service.SkillService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalServiceImpl implements GoalService {
    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final GoalMapper goalMapper;
    private final GoalCompletedEventPublisher goalCompletedEventPublisher;

    @Override
    @Transactional
    public GoalDto updateGoal(long goalId, GoalDto goalDto) {
        Goal existingGoal = goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Goal not found with id: " + goalId));

        Goal updatedGoal = validateAndSetGoalForUpdate(existingGoal, goalDto);
        goalRepository.save(updatedGoal);
        log.info("Goal with ID: {} successfully updated and saved.", goalId);

        List<User> users = goalRepository.findUsersByGoalId(goalId);
        skillService.addSkillToUsers(users, goalId);

        String userIds = users.stream()
                .map(user -> String.valueOf(user.getId()))
                .collect(Collectors.joining(", "));

        log.info("Skills added to users for goal ID: {}. User IDs: [{}]", goalId, userIds);

        users.forEach(user -> {
            goalCompletedEventPublisher.publish(new GoalCompletedEvent(user.getId(), goalId));
            log.info("GoalCompletedEvent published for user ID: {} and goal ID: {}", user.getId(), goalId);
        });

        return goalMapper.toGoalDto(updatedGoal);
    }

    @Override
    public GoalDto getGoal(long goalId) {

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Goal not found with id: " + goalId));

        return goalMapper.toGoalDto(goal);
    }

    private Goal validateAndSetGoalForUpdate(Goal existingGoal, GoalDto goalDto) {
        Goal updatedGoal = goalMapper.toGoal(goalDto);

        if (existingGoal.getStatus() == GoalStatus.COMPLETED) {
            throw new DataValidationException("Cannot update because the goal is already in 'COMPLETED' status");
        }

        existingGoal.setStatus(updatedGoal.getStatus());
        existingGoal.setTitle(updatedGoal.getTitle());
        existingGoal.setSkillsToAchieve(updatedGoal.getSkillsToAchieve());
        existingGoal.setDescription(updatedGoal.getDescription());

        return existingGoal;
    }
}
