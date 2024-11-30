package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalValidator {
    private static final int MAX_NUMBER_TARGET = 3;

    private final GoalInvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;


    public void validateCorrectnessPlayers(GoalInvitationDto invitationDto) {
        if (invitationDto.getInvitedUserId() == null || invitationDto.getInviterId() == null) {
            throw new IllegalArgumentException("Приглашающий и приглашенный игроки должны быть указаны");
        }

        if (invitationDto.getInvitedUserId().equals(invitationDto.getInviterId())) {
            throw new IllegalArgumentException("Приглашающий и приглашенный игрок не должен быть одним пользователем");
        }

        if (!invitationRepository.existsById(invitationDto.getInvitedUserId()) ||
                !invitationRepository.existsById(invitationDto.getInviterId())) {
            throw new IllegalArgumentException("Игроки должны существовать в бд");
        }
    }

    public void validateUsersAndGoals(GoalInvitation goalInvitation) {
        User invited = goalInvitation.getInvited();
        Goal goal = goalInvitation.getGoal();
        List<Goal> goals = invited.getGoals();


        if (!goalRepository.existsById(goal.getId())) {
            throw new IllegalArgumentException("Цели не существует");
        }

        if (goals.size() >= 3) {
            throw new IllegalArgumentException("У вас максимальное количество целей");
        }

        if (goals.contains(goal)) {
            throw new IllegalArgumentException("Вы уже работаете над целью");
        }
    }

    public void validateExistGoal(long id) {
        if (!invitationRepository.existsById(id)) {
            throw new IllegalArgumentException("Цели не существует");
        }
    }
}
