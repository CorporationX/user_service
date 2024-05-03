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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GoalInvitationService {
    final GoalInvitationRepository goalInvitationRepository;
    final GoalRepository goalRepository;
    final UserRepository userRepository;
    final GoalInvitationMapper goalInvitationMapper;
    GoalInvitation goalInvitation;
    RuntimeException runtimeException = new RuntimeException();

    public GoalInvitationDto createInvitation(GoalInvitationDto goalInvitationDto) {
        if (goalInvitationDto.getInviterId() != null && goalInvitationDto.getInvitedUserId() != null) {
            if (!goalInvitationDto.getInviterId().equals(goalInvitationDto.getInvitedUserId())) {
                if (userRepository.existsById(goalInvitationDto.getInviterId()) && userRepository.existsById(goalInvitationDto.getInvitedUserId())) {
                    goalInvitation = goalInvitationMapper.toEntity(goalInvitationDto);

                    goalInvitation.setInviter(userRepository.findById(goalInvitationDto.getInviterId()).get());
                    goalInvitation.setInvited(userRepository.findById(goalInvitationDto.getInvitedUserId()).get());
                    goalInvitation.setGoal(goalRepository.findById(goalInvitationDto.getGoalId()).get());

                    goalInvitationRepository.save(goalInvitation);
                    return goalInvitationMapper.toDto(goalInvitation);
                } else {
                    throw new RuntimeException("There is no such inviter or invitedUser in database");
                }
            } else {
                throw new RuntimeException("InviterId equals InvitedUserId");
            }
        } else {
            throw new RuntimeException("InviterId == null or InvitedUserId == null");
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
