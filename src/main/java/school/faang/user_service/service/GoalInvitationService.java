package school.faang.user_service.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
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

    public RequestStatus acceptGoalInvitation(long id) {
        if (goalInvitationRepository.findById(id).isPresent()) {
            goalInvitation = goalInvitationRepository.findById(id).get();
            if (goalRepository.findById(goalInvitation.getGoal().getId()).isPresent()) {
                Goal goal = goalRepository.findById(goalInvitation.getGoal().getId()).get();
                List<Goal> setGoals = goalInvitation.getInvited().getSetGoals();
                if (setGoals.size() < 3) {
                    if (!setGoals.contains(goal)) {
                        goalInvitation.getInvited().getGoals().add(goal);
                        return RequestStatus.ACCEPTED;
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

    public RequestStatus rejectGoalInvitation(long id) {
        if (goalInvitationRepository.findById(id).isPresent()) {
            goalInvitation = goalInvitationRepository.findById(id).get();
            if (goalRepository.findById(goalInvitation.getGoal().getId()).isPresent()) {
                return RequestStatus.REJECTED;
            } else {
                throw runtimeException;
            }
        } else {
            throw runtimeException;
        }
    }

}
