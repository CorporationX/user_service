package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.GoalInvitationDto;
import school.faang.user_service.dto.InvitationFilterIDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exceptions.GoalInvitationWasNotFoundException;
import school.faang.user_service.exceptions.UserWasNotFoundException;
import school.faang.user_service.filters.interfaces.GoalsFilter;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final List<GoalsFilter> filters;
    private static final int MAX_USER_GOALS = 3;

    @Transactional
    public GoalInvitation createInvitation(GoalInvitationDto goalInvitationDto) {
        long inviterId = goalInvitationDto.inviterId();
        long invitedId = goalInvitationDto.invitedUserId();
        long goalId = goalInvitationDto.goalId();

        if (inviterId == invitedId) {
            throw new IllegalArgumentException("The invited user is a inviting user!");
        }
        if (!isUserInDatabase(invitedId) || !isUserInDatabase(inviterId)) {
            throw new IllegalArgumentException("There is user that is not in database!");
        }

        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(goalInvitationDto);
        User invitingUser = findUserById(inviterId);
        User invitedUser = findUserById(invitedId);
        Goal inviterGoal = findGoalById(goalId);

        goalInvitation.setGoal(inviterGoal);
        goalInvitation.setCreatedAt(LocalDateTime.now());
        goalInvitation.setUpdatedAt(LocalDateTime.now());
        inviterGoal.getInvitations().add(goalInvitation);

        invitingUser.getSentGoalInvitations().add(goalInvitation);
        invitedUser.getReceivedGoalInvitations().add(goalInvitation);

        goalInvitationRepository.save(goalInvitation);
        userRepository.save(invitedUser);
        userRepository.save(invitingUser);

        return goalInvitation;
    }

    @Transactional
    public GoalInvitation acceptGoalInvitation(long id) {
        GoalInvitation goalInvitation = findGoalInvitationById(id);
        Goal recievedGoal = goalInvitation.getGoal();
        User acceptingUser = goalInvitation.getInvited();

        long userGoals = acceptingUser.getGoals().size();
        if (userGoals >= MAX_USER_GOALS) {
            throw new IllegalArgumentException("User already has max amount of goals");
        }
        if (recievedGoal.getUsers().contains(acceptingUser)) {
            throw new IllegalArgumentException("User already working on this goal, userID : " + acceptingUser.getId());
        }

        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        acceptingUser.getGoals().add(recievedGoal);
        goalInvitation.setUpdatedAt(LocalDateTime.now());
        recievedGoal.getUsers().add(acceptingUser);
        recievedGoal.setUpdatedAt(LocalDateTime.now());

        userRepository.save(acceptingUser);
        goalInvitationRepository.save(goalInvitation);
        goalRepository.save(recievedGoal);

        return goalInvitation;
    }

    @Transactional
    public GoalInvitation rejectGoalInvitation(long id) {
        GoalInvitation goalInvitation = findGoalInvitationById(id);
        Goal recievedGoal = goalInvitation.getGoal();

        goalInvitation.setStatus(RequestStatus.REJECTED);
        goalInvitation.setUpdatedAt(LocalDateTime.now());
        recievedGoal.getInvitations().remove(goalInvitation);

        goalInvitationRepository.save(goalInvitation);
        goalRepository.save(recievedGoal);

        return goalInvitation;
    }

    @Transactional
    public List<GoalInvitation> getInvitations(InvitationFilterIDto filter) {
        Stream<GoalInvitation> invitations = goalInvitationRepository.findAll().stream();
        return filterInvitations(filter, invitations);
    }

    private List<GoalInvitation> filterInvitations(InvitationFilterIDto invitationFilter,
                                                   Stream<GoalInvitation> invitations) {
        for (GoalsFilter filter : filters) {
            if (filter.isAcceptable(invitationFilter)) {
                invitations = filter.accept(invitations, invitationFilter);
            }
        }
        return invitations.toList();
    }

    private GoalInvitation findGoalInvitationById(long id) {
        return goalInvitationRepository.findById(id)
                .orElseThrow(() -> new GoalInvitationWasNotFoundException("Goal invite was not found, ID : " +  id));
    }

    private Goal findGoalById(long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("There is no goal with id : " + goalId));
    }

    private boolean isUserInDatabase(long userId) {
        return userRepository.findById(userId).isPresent();
    }

    private User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserWasNotFoundException("User was not found. ID : " + id));
    }
}
