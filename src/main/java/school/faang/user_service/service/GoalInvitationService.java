package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.GoalInvitationException;
import school.faang.user_service.filter.filtersForGoalInvitation.GoalInvitationFilter;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final List<GoalInvitationFilter> goalInvitationFilters;
    private static final int maxGoals = 3;

    @Transactional
    public GoalInvitationDto createInvitation(GoalInvitationDto invitationDto) {
        User inviter = userRepository.findById(invitationDto.getInviterId()).get();
        User invited = userRepository.findById(invitationDto.getInvitedUserId()).get();

        if (!userRepository.existsById(invitationDto.getInviterId())
                && !userRepository.existsById(invitationDto.getInvitedUserId())) {
            throw new GoalInvitationException("InvationDto missing from the database");
        }

        if (invited.equals(inviter)) {
            throw new GoalInvitationException("You cannot specify the same ID for the invitee and the inviter");
        }

        Goal goal = goalRepository.findById(invitationDto.getGoalId()).orElseThrow(() -> new GoalInvitationException("goal not found"));

        GoalInvitation savedGoalInvitation = goalInvitationRepository.save(new GoalInvitation(
                new GoalInvitation().getId(),
                goal,
                inviter,
                invited,
                invitationDto.getStatus(),
                LocalDateTime.now(),
                LocalDateTime.now()));

        return goalInvitationMapper.toDto(savedGoalInvitation);
    }


    public GoalInvitationDto acceptGoalInvitation(long id) {
        GoalInvitation goalInvitation = getGoalInvitation(id);
        User user = goalInvitation.getInvited();

        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        user.getGoals().add(goalInvitation.getGoal());
        userRepository.save(user);
        goalInvitationRepository.save(goalInvitation);
        return goalInvitationMapper.toDto(goalInvitation);
    }

    public GoalInvitationDto rejectGoalInvitation(long id) {

        GoalInvitation goalInvitation = getGoalInvitation(id);
        goalInvitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(goalInvitation);
        return goalInvitationMapper.toDto(goalInvitation);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filters) {
        List<GoalInvitation> invitations = (List<GoalInvitation>) goalInvitationRepository.findAll();
        return invitations.isEmpty() ? Collections.emptyList() : applyFilter(invitations.stream(), filters);
    }

    private List<GoalInvitationDto> applyFilter(Stream<GoalInvitation> goalInvitationStream, InvitationFilterDto filterDto) {
        List<GoalInvitation> invitations = new ArrayList<>();
        List<GoalInvitationFilter> requiredFilters = goalInvitationFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .toList();
        for (GoalInvitationFilter filter : requiredFilters) {
            invitations = filter.apply(goalInvitationStream, filterDto);
        }
        return invitations.stream().map(goalInvitationMapper::toDto).toList();
    }

    private GoalInvitation getGoalInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(id)
                .orElseThrow(() -> new GoalInvitationException("GoalInvitation not found"));
        User user = goalInvitation.getInvited();
        Goal goal = Optional.of(goalInvitation.getGoal())
                .orElseThrow(() -> new GoalInvitationException("goal not found"));
        if (!user.getGoals().contains(goalInvitation.getGoal()) && user.getGoals().size() > maxGoals) {
            throw new GoalInvitationException("The user is already in the goal or he is already participating in three goals");
        }
        if (goal == null) {
            throw new GoalInvitationException("goal equals null");
        }
        return goalInvitation;
    }
}
