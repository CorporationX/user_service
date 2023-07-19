package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final GoalMapper goalMapper;

    @Transactional
    public UpdateGoalDto updateGoal(UpdateGoalDto updateGoalDto) {
        List<SkillDto> skillDtos = updateGoalDto.getSkillDtos();
        List<Long> userIds = updateGoalDto.getUserIds();
        Goal goalToUpdate = goalRepository.findById(updateGoalDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        validateUpdate(goalToUpdate, updateGoalDto, skillDtos);


        skillDtos.forEach(skill -> {
            userIds.forEach(userId -> {
                User user = userRepository.findById(userId).orElseThrow(() ->
                        new IllegalArgumentException("User not found"));
                List<String> userSkills = user.getSkills().stream().map(Skill::getTitle).toList();
                if (!userSkills.contains(skill.getTitle())) {
                    skillRepository.assignSkillToUser(skill.getId(), user.getId());
                }
            });
        });

        goalToUpdate.setStatus(GoalStatus.COMPLETED);
        goalToUpdate.setUpdatedAt(LocalDateTime.now());
        return goalMapper.goalToUpdateGoalDto(goalRepository.save(goalToUpdate));
    }

    private void validateUpdate(Goal goalToUpdate, UpdateGoalDto updateGoalDto, List<SkillDto> skillDtos) {
        if (updateGoalDto.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title cannot be blank");
        }

        if (goalToUpdate.getStatus().equals(GoalStatus.COMPLETED)) {
            throw new IllegalArgumentException("Goal already completed");
        }

        skillDtos.forEach(skillToAchieve -> {
            if (!skillRepository.existsByTitle(skillToAchieve.getTitle())) {
                throw new IllegalArgumentException("Skill " + skillToAchieve.getTitle() + " not found");
            }
        });
    }
}
