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
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GoalInvitationService {
    final GoalInvitationRepository goalInvitationRepository;
    final GoalInvitationMapper goalInvitationMapper;
    final GoalRepository goalRepository;
    GoalInvitation goalInvitation;
    RuntimeException runtimeException = new RuntimeException();

    public void createInvitation(GoalInvitationDto goalInvitationDto) {
        if (goalInvitationDto.getInviterId() != null && goalInvitationDto.getInvitedUserId() != null) {
            if (!goalInvitationDto.getInviterId().equals(goalInvitationDto.getInvitedUserId())) {
                if (goalInvitationRepository.existsById(goalInvitationDto.getInviterId()) && goalInvitationRepository.existsById(goalInvitationDto.getInvitedUserId())) {
                    goalInvitationRepository.save(goalInvitationMapper.toEntity(goalInvitationDto));
                } else {
                    throw runtimeException;
                }
            } else {
                throw runtimeException;
            }
        } else {
            throw runtimeException;
        }
    }

    public void acceptGoalInvitation(long id) {
        if (goalInvitationRepository.findById(id).isPresent()) {
            goalInvitation = goalInvitationRepository.findById(id).get();
            if (goalRepository.findById(goalInvitation.getGoal().getId()).isPresent()) {
                Goal goal = goalRepository.findById(goalInvitation.getGoal().getId()).get();
                List<Goal> setGoals = goalInvitation.getInvited().getSetGoals();
                if (setGoals.size() < 3) {
                    if (!setGoals.contains(goal)) {
                        goalInvitation.setStatus(RequestStatus.ACCEPTED);
                        goalInvitation.getInvited().getGoals().add(goal);
                    } else {
                        throw runtimeException;
                    }
                } else {
                    throw runtimeException;
                }
            } else {
                throw runtimeException;
            }
        } else {
            throw runtimeException;
        }
    }

    public void rejectGoalInvitation(long id) {
        if (goalInvitationRepository.findById(id).isPresent()) {
            goalInvitation = goalInvitationRepository.findById(id).get();
            if (goalRepository.findById(goalInvitation.getGoal().getId()).isPresent()) {
                goalInvitation.setStatus(RequestStatus.REJECTED);
            } else {
                throw runtimeException;
            }
        } else {
            throw runtimeException;
        }
    }

    public List<GoalInvitation> getInvitations(InvitationFilterDto filter) {
        if (filter != null) {
            return goalInvitationRepository.findAll().stream().
                    filter(goalInvitation -> goalInvitation.getInvited().getId() == filter.getInvitedId()).
                    filter(goalInvitation -> goalInvitation.getInviter().getId() == filter.getInviterId()).
                    filter(goalInvitation -> goalInvitation.getInvited().getUsername().equals(filter.getInvitedNamePattern())).
                    filter(goalInvitation -> goalInvitation.getInviter().getUsername().equals(filter.getInviterNamePattern())).
                    filter(goalInvitation -> goalInvitation.getStatus().equals(filter.getStatus())).toList();
        } else {
            throw runtimeException;
        }
    }
}
