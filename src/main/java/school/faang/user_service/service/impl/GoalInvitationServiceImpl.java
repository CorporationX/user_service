package school.faang.user_service.service.impl;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.model.dto.GoalInvitationDto;
import school.faang.user_service.model.filter_dto.InvitationFilterDto;
import school.faang.user_service.model.enums.RequestStatus;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.Goal;
import school.faang.user_service.model.entity.GoalInvitation;
import school.faang.user_service.model.enums.GoalStatus;
import school.faang.user_service.exception.GoalInvitationValidationException;
import school.faang.user_service.filter.goal.GoalInvitationFilter;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.GoalInvitationRepository;
import school.faang.user_service.repository.GoalRepository;
import school.faang.user_service.service.GoalInvitationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class GoalInvitationServiceImpl implements GoalInvitationService {

    @Setter
    @Value("${app.max-active-user-goals}")
    private int maxActiveUserGoals;

    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;

    private final List<GoalInvitationFilter> goalInvitationFilters;
    private final GoalInvitationMapper mapper;

    public GoalInvitationServiceImpl(GoalInvitationRepository goalInvitationRepository, UserRepository userRepository,
                                     GoalRepository goalRepository, List<GoalInvitationFilter> goalInvitationFilters,
                                     GoalInvitationMapper mapper) {
        this.goalInvitationRepository = goalInvitationRepository;
        this.userRepository = userRepository;
        this.goalRepository = goalRepository;
        this.goalInvitationFilters = goalInvitationFilters;
        this.mapper = mapper;
    }

    @Override
    public GoalInvitationDto createInvitation(GoalInvitationDto goalInvitationDto) {

        Long inviterId = goalInvitationDto.getInviterId();
        Long invitedUserId = goalInvitationDto.getInvitedUserId();

        if (inviterId.equals(invitedUserId)) {
            throw new GoalInvitationValidationException("User invited and user inviter cannot be equals, id " + invitedUserId);
        }

        Optional<User> optionalUserInviter = userRepository.findById(inviterId);
        if (optionalUserInviter.isEmpty()) {
            throw new GoalInvitationValidationException("User inviter with id " + inviterId + " user does not exist");
        }

        Optional<User> optionalUserInvited = userRepository.findById(invitedUserId);
        if (optionalUserInvited.isEmpty()) {
            throw new GoalInvitationValidationException("User invited with id " + invitedUserId + " user does not exist");
        }

        Long goalId = goalInvitationDto.getGoalId();
        Optional<Goal> optionalGoal = goalRepository.findById(goalId);
        if (optionalGoal.isEmpty()) {
            throw new GoalInvitationValidationException("Goal with id " + goalId + " does not exist");
        }

        GoalInvitation entity = mapper.toEntity(goalInvitationDto);

        entity.setInviter(optionalUserInviter.get());
        entity.setInvited(optionalUserInvited.get());
        entity.setGoal(optionalGoal.get());
        entity.setStatus(RequestStatus.PENDING);

        GoalInvitation saved = goalInvitationRepository.save(entity);

        return mapper.toDto(saved);
    }

    @Override
    public void acceptGoalInvitation(long id) {

        Optional<GoalInvitation> optionalGoalInvitation = goalInvitationRepository.findById(id);
        if (optionalGoalInvitation.isEmpty()) {
            throw new GoalInvitationValidationException("GoalInvitation with id " + id + " does not exist");
        }

        GoalInvitation goalInvitation = optionalGoalInvitation.get();

        RequestStatus invitationStatus = goalInvitation.getStatus();
        if (invitationStatus != RequestStatus.PENDING) {
            throw new GoalInvitationValidationException("Goal with id " + id + " is not in PENDING status (current status = " + invitationStatus + ")");
        }

        User invitedUser = goalInvitation.getInvited();
        List<Goal> invitedUserGoals = invitedUser.getGoals();

        if (invitedUserGoals.stream().anyMatch(goal -> goal.getId() == id)) {
            throw new GoalInvitationValidationException("User with id " + invitedUser.getId() + " already has goal with id " + id);
        }

        long activeGoalsAmount = invitedUserGoals.stream().filter(g -> g.getStatus() == GoalStatus.ACTIVE).count();
        if (activeGoalsAmount >= maxActiveUserGoals) {
            throw new GoalInvitationValidationException("User with id " + invitedUser.getId() + " has more or equals then " + maxActiveUserGoals
                    + " active goals (current amount = " + activeGoalsAmount + ")");
        }

        goalInvitationRepository.updateStatusById(RequestStatus.ACCEPTED, id);

        Goal goal = goalInvitation.getGoal();
        goal.getUsers().add(invitedUser);
        goalRepository.save(goal);
    }

    @Override
    public void rejectGoalInvitation(long id) {
        Optional<GoalInvitation> optionalGoalInvitation = goalInvitationRepository.findById(id);
        if (optionalGoalInvitation.isEmpty()) {
            throw new GoalInvitationValidationException("GoalInvitation with id " + id + " does not exist");
        }

        GoalInvitation goalInvitation = optionalGoalInvitation.get();
        RequestStatus invitationStatus = goalInvitation.getStatus();
        if (invitationStatus == RequestStatus.REJECTED) {
            throw new GoalInvitationValidationException("Goal with id " + id + " is already in REJECTED status");
        }

        goalInvitationRepository.updateStatusById(RequestStatus.REJECTED, id);
    }

    @Override
    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filterDto) {
        Stream<GoalInvitation> goalInvitations = new ArrayList<>(goalInvitationRepository.findAll()).stream();
        return goalInvitationFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(goalInvitations,
                        (stream, filter) -> filter.apply(stream, filterDto),
                        (s1, s2) -> s1)
                .map(mapper::toDto)
                .toList();
    }

}
