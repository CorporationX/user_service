package school.faang.user_service.service.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.QGoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.GoalInvitationService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalInvitationServiceImpl implements GoalInvitationService {
    private static final String NOT_FOUND = "Not found %s by id %d";

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalInvitationMapper goalInvitationMapper;

    @Transactional
    @Override
    public GoalInvitationDto createInvitation(Long inviterId, GoalInvitationDto invitation) {
        if (inviterId.equals(invitation.getInvitedUserId())) {
            throw new IllegalArgumentException("The user cannot invite himself to the goal");
        }
       Goal goal = goalRepository.findById(invitation.getGoalId())
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND, "goal", invitation.getGoalId())));
       User inviter =  userRepository.findById(inviterId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND, "inviter", inviterId)));
       User invited =  userRepository.findById(invitation.getInvitedUserId())
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND, "invited",
                        invitation.getInvitedUserId())));
        GoalInvitation goalInvitation = goalInvitationRepository
                .save(goalInvitationMapper.toEntity(invitation, goal, inviter, invited));
        return goalInvitationMapper.toDto(goalInvitation);
    }

    @Transactional
    @Override
    public GoalInvitationDto acceptGoalInvitation(Long goalInvitationId, Long invitedId) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(goalInvitationId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND, "goalInvitation", goalInvitationId)));
        User invited = userRepository.findById(invitedId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND, "invited", invitedId)));
        if (!invitedId.equals(goalInvitation.getInvited().getId())) {
            throw new IllegalArgumentException("Сan only confirm your own invitation");
        }
        if (invited.getGoals().size() >= 3) {
            throw new IllegalArgumentException("Users can not accept the invitation, " +
                    "maximum number of active goals (max = 3)");
        }
        if (goalInvitation.getStatus() == RequestStatus.ACCEPTED) {
            throw new IllegalArgumentException("The invited user is already working on this goal.");
        }
        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        goalInvitation.setUpdatedAt(LocalDateTime.now());
        invited.getGoals().add(goalInvitation.getGoal());
        userRepository.save(invited);
        return goalInvitationMapper.toDto(goalInvitationRepository.save(goalInvitation));
    }

    @Transactional
    @Override
    public GoalInvitationDto rejectGoalInvitation(Long goalInvitationId, Long invitedId) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(goalInvitationId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND, "goalInvitation", goalInvitationId)));
        userRepository.findById(invitedId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND, "invited", invitedId)));
        if (goalInvitation.getStatus() == RequestStatus.PENDING) {
            goalInvitation.setStatus(RequestStatus.REJECTED);
        } else {
            throw new IllegalArgumentException("Can only cancel an invitation when the status is PENDING");
        }
        return goalInvitationMapper.toDto(goalInvitationRepository.save(goalInvitation));
    }

    @Transactional
    @Override
    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filter, Integer from, Integer size) {
        BooleanExpression finalCondition = getCondition(filter);
        Sort sort = Sort.by("id").ascending();
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);
        Page<GoalInvitation> goalInvitations;
        if (finalCondition == null) {
            goalInvitations = goalInvitationRepository.findAll(pageRequest);
        } else {
            goalInvitations = goalInvitationRepository.findAll(finalCondition, pageRequest);
        }
        return goalInvitationMapper.toDtos(goalInvitations.stream().toList());
    }

    private BooleanExpression getCondition(InvitationFilterDto filter) {
        QGoalInvitation qGoalInvitation = QGoalInvitation.goalInvitation;
        List<BooleanExpression> conditions = new ArrayList<>();
        if (filter.getInviterNamePattern() != null) {
            conditions.add(qGoalInvitation.inviter.username.like("%" + filter.getInviterNamePattern() + "%"));
        }
        if (filter.getInvitedNamePattern() != null) {
            conditions.add(qGoalInvitation.invited.username.like("%" + filter.getInvitedNamePattern() + "%"));
        }
        if (filter.getInviterId() != null) {
            conditions.add(qGoalInvitation.inviter.id.eq(filter.getInviterId()));
        }
        if (filter.getInvitedId() != null) {
            conditions.add(qGoalInvitation.invited.id.eq(filter.getInvitedId()));
        }
        if (filter.getStatus() != null) {
            conditions.add(qGoalInvitation.status.eq(filter.getStatus()));
        }
        if (conditions.isEmpty()) {
            return null;
        } else {
            return conditions.stream()
                    .reduce(BooleanExpression::and)
                    .get();
        }
    }
}