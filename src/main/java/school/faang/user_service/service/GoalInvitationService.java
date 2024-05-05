package school.faang.user_service.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

import static school.faang.user_service.exception.MessageForGoalInvitationService.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GoalInvitationService {
    final GoalInvitationRepository goalInvitationRepository;
    final GoalRepository goalRepository;
    final UserRepository userRepository;
    final GoalInvitationMapper goalInvitationMapper;
    static final int SETGOALSIZE = 3;
    GoalInvitation goalInvitation;

    public GoalInvitationDto createInvitation(GoalInvitationDto goalInvitationDto) {
        if (goalInvitationDto == null) {
            throw new RuntimeException(INPUT_IS_NULL.getMessage());
        }
        goalInvitation = goalInvitationMapper.toEntity(goalInvitationDto);
        Long inviterId = goalInvitationDto.getInviterId();
        Long invitedUserId = goalInvitationDto.getInvitedUserId();
        goalInvitation.setInviter(userRepository.findById(inviterId).orElseThrow(() -> new RuntimeException(INVITER_ID_IS_NULL.getMessage())));
        goalInvitation.setInvited(userRepository.findById(invitedUserId).orElseThrow(() -> new RuntimeException(INVITED_USER_ID_IS_NULL.getMessage())));
        if (inviterId.equals(invitedUserId)) {
            throw new RuntimeException(INVITER_ID_EQUALS_INVITED_USER_ID.getMessage());
        }
        goalInvitation.setGoal(goalRepository.findById(goalInvitationDto.getGoalId()).orElseThrow(() -> new RuntimeException(NO_GOAL_IN_DB.getMessage())));

        goalInvitationRepository.save(goalInvitation);
        return goalInvitationMapper.toDto(goalInvitation);
    }

    public void acceptGoalInvitation(long id) {
        goalInvitation = goalInvitationRepository.findById(id).orElseThrow(() -> new RuntimeException(NO_GOAL_INVITATION_IN_DB.getMessage()));
        Goal goal = goalRepository.findById(goalInvitation.getGoal().getId()).orElseThrow(() -> new RuntimeException(NO_GOAL_IN_DB.getMessage()));
        List<Goal> setGoals = goalInvitation.getInvited().getSetGoals();
        if (setGoals.size() > SETGOALSIZE) {
            throw new RuntimeException(MORE_THEN_THREE_GOALS.getMessage());
        }
        if (setGoals.contains(goal)) {
            throw new RuntimeException(INVITED_HAS_GOAL.getMessage());
        }
        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        goalInvitation.getInvited().getGoals().add(goal);
        goalInvitationRepository.save(goalInvitation);
    }

    public void rejectGoalInvitation(long id) {
        goalInvitation = goalInvitationRepository.findById(id).orElseThrow(() -> new RuntimeException(NO_GOAL_INVITATION_IN_DB.getMessage()));
        goalRepository.findById(goalInvitation.getGoal().getId()).orElseThrow(() -> new RuntimeException(NO_GOAL_IN_DB.getMessage()));
        goalInvitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(goalInvitation);
    }

    public List<GoalInvitation> getInvitations(InvitationFilterDto filter) {
        if (filter == null) {
            throw new RuntimeException(INPUT_IS_NULL.getMessage());
        }
        return goalInvitationRepository.findAll().stream().
                filter(goalInvitation -> goalInvitation.getInvited() != null && goalInvitation.getInviter() != null &&
                        goalInvitation.getInvited().getUsername() != null && goalInvitation.getInviter().getUsername() != null).
                filter(goalInvitation -> goalInvitation.getInvited().getId() == filter.getInvitedId()).
                filter(goalInvitation -> goalInvitation.getInviter().getId() == filter.getInviterId()).
                filter(goalInvitation -> goalInvitation.getInvited().getUsername().equals(filter.getInvitedNamePattern())).
                filter(goalInvitation -> goalInvitation.getInviter().getUsername().equals(filter.getInviterNamePattern())).
                toList();
    }
}
