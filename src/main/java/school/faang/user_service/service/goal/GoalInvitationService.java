package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DuplicateObjectException;
import school.faang.user_service.exception.GoalInvitationStatusException;
import school.faang.user_service.exception.GoalInvitationValidationException;
import school.faang.user_service.exception.ValueExceededException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.filter.GoalInvitationFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoalInvitationService {

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final List<GoalInvitationFilter> goalInvitationFilters;

    @Value("${application.constants.max-active-goals-count}")
    private int maxActiveGoalsCount;

    public GoalInvitationDto createInvitation(GoalInvitationDto goalInvitationDto) {
        if (goalInvitationDto.getInviterId().equals(goalInvitationDto.getInvitedUserId())) {
            throw new GoalInvitationValidationException("User inviter and invited user are equal.");
        }

        User inviter = userRepository.findById(goalInvitationDto.getInviterId())
                .orElseThrow(() -> new EntityNotFoundException("User not found by ID: " + goalInvitationDto.getInviterId()));
        User invited = userRepository.findById(goalInvitationDto.getInvitedUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found by ID: " + goalInvitationDto.getInvitedUserId()));
        Goal goal = goalRepository.findById(goalInvitationDto.getGoalId())
                .orElseThrow(() -> new EntityNotFoundException("Goal not found by ID: " + goalInvitationDto.getGoalId()));

        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(goalInvitationDto);
        goalInvitation.setGoal(goal);
        goalInvitation.setInviter(inviter);
        goalInvitation.setInvited(invited);
        goalInvitation.setStatus(RequestStatus.PENDING);
        GoalInvitation savedGoalInvitation = goalInvitationRepository.save(goalInvitation);
        log.info("Goal invitation was successfully saved under ID: " + savedGoalInvitation.getId());
        return goalInvitationMapper.toDto(savedGoalInvitation);
    }

    @Transactional
    public GoalInvitationDto acceptGoalInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Goal invitation not found by ID: " + id));
        if (!goalInvitation.getStatus().equals(RequestStatus.PENDING)) {
            throw new GoalInvitationStatusException("The goal invitation status is already " + goalInvitation.getStatus());
        }
        Goal goal = goalRepository.findById(goalInvitation.getGoal().getId())
                .orElseThrow(() -> new EntityNotFoundException("Goal not found by ID: " + goalInvitation.getGoal().getId()));
        if (userRepository.findById(goalInvitation.getInviter().getId()).isEmpty()) {
            throw new EntityNotFoundException("User not found by ID: " + goalInvitation.getInviter().getId());
        }
        User invited = userRepository.findById(goalInvitation.getInvited().getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found by ID: " + goalInvitation.getInvited().getId()));

        List<Goal> userGoals = invited.getGoals();
        if (userGoals.contains(goal)) {
            throw new DuplicateObjectException("User with ID: " + invited.getId() + " already has the goal with ID: " + goal.getId());
        }
        if (userGoals.size() >= maxActiveGoalsCount) {
            throw new ValueExceededException("The maximum number: " + maxActiveGoalsCount + " of active goals has been exceeded for user with ID: " + invited.getId());
        }
        userGoals.add(goal);
        userRepository.save(invited);

        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        goalInvitation.setUpdatedAt(LocalDateTime.now());
        GoalInvitation acceptedGoalInvitation = goalInvitationRepository.save(goalInvitation);
        log.info("The goal invitation with ID: " + goalInvitation.getId() + " was accepted by the user with ID: " + invited.getId());
        return goalInvitationMapper.toDto(acceptedGoalInvitation);
    }

    public GoalInvitationDto rejectGoalInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Goal invitation not found by ID: " + id));
        if (!goalInvitation.getStatus().equals(RequestStatus.PENDING)) {
            throw new GoalInvitationStatusException("The goal invitation status is already " + goalInvitation.getStatus());
        }
        if (goalRepository.findById(goalInvitation.getGoal().getId()).isEmpty()) {
            throw new EntityNotFoundException("Goal not found by ID: " + goalInvitation.getGoal().getId());
        }
        if (userRepository.findById(goalInvitation.getInviter().getId()).isEmpty()) {
            throw new EntityNotFoundException("User not found by ID: " + goalInvitation.getInviter().getId());
        }
        if (userRepository.findById(goalInvitation.getInvited().getId()).isEmpty()) {
            throw new EntityNotFoundException("User not found by ID: " + goalInvitation.getInvited().getId());
        }

        goalInvitation.setStatus(RequestStatus.REJECTED);
        goalInvitation.setUpdatedAt(LocalDateTime.now());
        GoalInvitation rejectedGoalInvitation = goalInvitationRepository.save(goalInvitation);
        log.info("The goal invitation with ID: " + goalInvitation.getId() + " was rejected by the user with ID: " + goalInvitation.getInvited());
        return goalInvitationMapper.toDto(rejectedGoalInvitation);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filters) {
        Stream<GoalInvitation> goalInvitationsStream = goalInvitationRepository.findAll().stream();
        goalInvitationsStream = goalInvitationFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(goalInvitationsStream, (stream, filter) -> filter.apply(stream, filters), (s1, s2) -> s1);
        List<GoalInvitation> filteredGoalInvitations = goalInvitationsStream.toList();
        log.info("Received goal invitations filtered by: inviterNamePattern='{}', invitedNamePattern='{}', inviterId={}, invitedId={}, status={}",
                filters.getInviterNamePattern(),
                filters.getInvitedNamePattern(),
                filters.getInviterId(),
                filters.getInvitedId(),
                filters.getStatus());
        return goalInvitationMapper.toDto(filteredGoalInvitations);
    }
}
