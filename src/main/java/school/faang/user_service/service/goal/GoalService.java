package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoalService {
    public static final int MAX_ACTIVE_GOALS = 3;
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filter) {
        validateId(userId);

        Stream<Goal> goalStream = goalRepository.findGoalsByUserId(userId);

        if (filter == null) {
            return goalStream.map(goalMapper::toDto).toList();
        }

        for (GoalFilter goalFilter : goalFilters) {
            if (goalFilter.isApplicable(filter)) {
                goalStream = goalFilter.apply(goalStream, filter);
            }
        }

        return goalStream.map(goalMapper::toDto).toList();
    }

    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        validateId(userId);
        validateGoal(goalDto);
        validateAdditionGoalToUser(userId, goalDto);

        Goal goal = goalMapper.toEntity(goalDto);
        convertDtoDependenciesToEntity(goalDto, goal);

        goalRepository.save(goal);

        return goalMapper.toDto(goal);
    }

    private void validateId(Long userId) {
        if (userId == null) {
            throw new DataValidationException("User id cannot be null!");
        }
        if (userId <= 0) {
            throw new DataValidationException("User id cannot be less than 1!");
        }
    }

    private void validateGoal(GoalDto goalDto) {
        if (goalDto == null) {
            throw new DataValidationException("Goal cannot be null!");
        }
        if (goalDto.getTitle() == null || goalDto.getTitle().isBlank()) {
            throw new DataValidationException("Title of goal cannot be empty!");
        }
    }

    private void validateAdditionGoalToUser(Long userId, GoalDto goalDto) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new DataValidationException("User with given id was not found!");
        }

        if (optionalUser.get().getGoals() == null ||goalDto.getStatus() == GoalStatus.COMPLETED) {
            return;
        }

        List<Goal> activeGoals = optionalUser.get().getGoals().stream()
                .filter(goal -> goal.getStatus() == GoalStatus.ACTIVE)
                .toList();

        if (activeGoals.size() >= MAX_ACTIVE_GOALS) {
            throw new DataValidationException("User cannot have more than " + MAX_ACTIVE_GOALS + " active goals at the same time!");
        }
    }

    private void convertDtoDependenciesToEntity(GoalDto goalDto, Goal goal) {
        if (goalDto.getMentorId() != null) {
            Optional<User> mentorOptional = userRepository.findById(goalDto.getMentorId());
            if (mentorOptional.isEmpty()) {
                throw new DataValidationException("Mentor with given id was not found!");
            }
            goal.setMentor(mentorOptional.get());
        }

        if (goalDto.getParentId() != null) {
            Optional<Goal> goalOptional = goalRepository.findById(goalDto.getParentId());
            if (goalOptional.isEmpty()) {
                throw new DataValidationException("Goal-parent with given id was not found!");
            }
            goal.setParent(goalOptional.get());
        }

        if (goalDto.getSkillIds() != null) {
            List<Skill> skills = new ArrayList<>();
            goalDto.getSkillIds().forEach(skillId -> {
                Optional<Skill> optionalSkill = skillRepository.findById(skillId);
                if (optionalSkill.isEmpty())
                    throw new DataValidationException("There is no way to add a goal with a non-existent skill!");
                skills.add(optionalSkill.get());
            });
            goal.setSkillsToAchieve(skills);
        }
    }
}
