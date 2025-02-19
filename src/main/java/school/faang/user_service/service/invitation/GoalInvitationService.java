package school.faang.user_service.service.invitation;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.invitation.GoalInvitationDto;
import school.faang.user_service.dto.invitation.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.adapter.UserRepositoryAdapter;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.specifications.GoalInvitationSpecification;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoalInvitationService {
  private final GoalInvitationRepository goalInvitationRepository;
  private final UserRepositoryAdapter userRepositoryAdapter;
  private final GoalInvitationMapper goalInvitationMapper;
  private final GoalRepository goalRepository; // TODO в другой задаче там есть уже GoalService

  @Value("${application.goals.max-active}")
  private int activeGoals;

  @Transactional
  public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
    validateInvitation(invitation);
    GoalInvitation entity = goalInvitationMapper.toEntity(invitation);
    User inviter = userRepositoryAdapter.getById(invitation.getInviterId());
    User invited = userRepositoryAdapter.getById(invitation.getInvitedUserId());
    entity.setInviter(inviter);
    entity.setInvited(invited);
    Goal goal =
        goalRepository
            .findById(invitation.getGoalId())
            .orElseThrow(
                () ->
                    new EntityNotFoundException("Goal not found by id: " + invitation.getGoalId()));
    entity.setGoal(goal);
    entity.setStatus(RequestStatus.PENDING);
    goalInvitationRepository.save(entity);
    return goalInvitationMapper.toDto(entity);
  }

    @Transactional
    public void acceptGoalInvitation(Long id) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("invitation not found by id: " + id));
        Goal goal = goalInvitation.getGoal();
        User invited = goalInvitation.getInvited();
        long userActiveGoals = invited.getGoals().stream()
                .filter(g -> g.getStatus() == GoalStatus.ACTIVE)
                .count();
        if (userActiveGoals >= activeGoals) {
            throw new DataValidationException("already have " + activeGoals + " active goals");
        }
        invited.getGoals().forEach(g -> {
            if (g.getId().equals(goal.getId())) {
                throw new DataValidationException("already have this goal");
            }
        });
        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        goal.addUser(invited);
    }

  @Transactional
  public void rejectGoalInvitation(Long id) {
    GoalInvitation goalInvitation =
        goalInvitationRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("invitation not found by id: " + id));
    goalInvitation.setStatus(RequestStatus.REJECTED);
  }

  public List<GoalInvitationDto> getInvitations(InvitationFilterDto filter) {
    List<Specification<GoalInvitation>> specs = new ArrayList<>();

    if (filter.getInvitedId() != null) {
      specs.add(GoalInvitationSpecification.getInvitedId(filter.getInvitedId()));
    }
    if (filter.getInviterId() != null) {
      specs.add(GoalInvitationSpecification.getInviterId(filter.getInviterId()));
    }
    if (filter.getInvitedNamePattern() != null) {
      specs.add(GoalInvitationSpecification.invitedNamePattern(filter.getInvitedNamePattern()));
    }
    if (filter.getInviterNamePattern() != null) {
      specs.add(GoalInvitationSpecification.inviterNamePattern(filter.getInviterNamePattern()));
    }
    if (filter.getStatus() != null) {
      specs.add(GoalInvitationSpecification.getByStatus(filter.getStatus()));
    }

    Specification<GoalInvitation> spec = specs.stream().reduce(Specification::and).orElse(null);

    List<GoalInvitation> goalInvitations = goalInvitationRepository.findAll(spec);
    return goalInvitationMapper.toDtoList(goalInvitations);
  }

  private void validateInvitation(GoalInvitationDto invitation) {
    if (invitation.getInvitedUserId() == null || invitation.getInviterId() == null) {
      throw new IllegalArgumentException("inviter or invited user must be filled");
    }
    if (invitation.getInvitedUserId().equals(invitation.getInviterId())) {
      throw new IllegalArgumentException("inviter and invited user can not be the same");
    }
  }
}
