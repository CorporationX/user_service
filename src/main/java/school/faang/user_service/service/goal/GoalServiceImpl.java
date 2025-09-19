package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;


import java.util.List;
import java.util.function.Predicate;

import static school.faang.user_service.entity.goal.GoalStatus.COMPLETED;

@Slf4j
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalMapper goalMapper;
    private final UserContext userContext;

    @Override
    public GoalDto create(CreateGoalDto createGoalDto) {
        User user = userRepository.getByIdOrThrow(userContext.getUserId());

        if (!user.getMentees().containsAll(createGoalDto.userIds())
                || (createGoalDto.userIds().size() == 1 && !createGoalDto.userIds().contains(user.getId()))) {
            throw new ForbiddenException("создать цель может либо ментор для своих менти, "
                    + "либо пользователь сам для себя");
        }

        if (!hasNoMoreThanTwoActiveGoals(user)) {
            throw new DataValidationException("Слишком много активных целей!");
        }

        Goal goal = goalMapper.toGoal(createGoalDto);
        goal.setMentor(userRepository.getByIdOrThrow(createGoalDto.mentorId()));
        goal.setUsers(userRepository.findAllById(createGoalDto.userIds()));

        goalRepository.save(goal);

        return goalMapper.toGoalDto(goal);
    }


    private boolean hasNoMoreThanTwoActiveGoals(User user) {
        long activeCount = user.getGoals().stream()
                .filter(goal -> goal.getStatus() == GoalStatus.ACTIVE)
                .limit(3)
                .count();
        return activeCount <= 2;
    }


    @Override
    public GoalDto update(long goalId, UpdateGoalDto updateGoalDto) {
        Goal goal = goalRepository.getByIdOrThrow(goalId);

        if (goal.getStatus().equals(COMPLETED)) {
            throw new DataValidationException("Цель Завершина");
        }

        if (!goal.getUsers().stream().map(User::getId).toList().contains(updateGoalDto.mentorId())) {
            throw new ForbiddenException("обновить цель может либо ментор цели, либо участник цели");
        }

        goalMapper.update(updateGoalDto, goal);
        goal = goalRepository.save(goal);
        return goalMapper.toGoalDto(goal);
    }

    @Override
    public void delete(long goalId) {
        Goal goal = goalRepository.getByIdOrThrow(goalId);
        long userId = userContext.getUserId();
        if (goal.getMentor().getId() != userId
                && !goal.getUsers().stream().map(User::getId).toList().contains(userId)) {
            throw new ForbiddenException("Удалить цель может либо ментор цели, либо участник цели");
        }

        goalRepository.deleteById(goalId);
    }

    @Override
    public List<GoalDto> getByFilters(GoalFilterDto filters) {
        if (filters.titleContains().isBlank()
                && filters.descriptionContains().isBlank()
                && filters.status() == null
                && filters.mentorId() == null) {
            log.warn("Значения поиска пусты!");
        }


        Predicate<Goal> predicate = str -> true;

        if (!filters.titleContains().isBlank()) {
            predicate = predicate.and(str -> str.equals(filters.titleContains()));
        }

        if (!filters.descriptionContains().isBlank()) {
            predicate = predicate.and(str -> str.equals(filters.descriptionContains()));
        }

        if (filters.status() != null) {
            predicate = predicate.and(str -> str.equals(filters.status()));
        }

        if (filters.mentorId() != null) {
            predicate = predicate.and(str -> str.equals(filters.mentorId()));
        }

        List<Goal> goals = goalRepository.findAll();

        return goals.stream().filter(predicate).map(goalMapper::toGoalDto).toList();
    }
}