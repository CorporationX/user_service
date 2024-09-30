package school.faang.user_service.service.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
import school.faang.user_service.filter.FilterInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.GoalInvitationService;
import school.faang.user_service.validator.ValidationInvitation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoalInvitationServiceImpl implements GoalInvitationService {
    private static final String NOT_FOUND = "Not found %s by id %d";

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final List<FilterInvitation<InvitationFilterDto, QGoalInvitation>> filterInvitations;
    private final ValidationInvitation validationInvitation;

    @Transactional
    @Override
    public GoalInvitationDto createInvitation(Long inviterId, GoalInvitationDto invitation) {
        validationInvitation.createInvitation(inviterId, invitation);
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
        validationInvitation.acceptGoalInvitation(goalInvitation, invited, invitedId);
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
        validationInvitation.rejectGoalInvitation(goalInvitation);
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

    private BooleanExpression getCondition(InvitationFilterDto invitationFilter) {
        QGoalInvitation qGoalInvitation = QGoalInvitation.goalInvitation;
        return filterInvitations.stream()
                .map(filter -> filter.getCondition(invitationFilter, qGoalInvitation))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .reduce(BooleanExpression::and)
                .orElse(null);
    }
}