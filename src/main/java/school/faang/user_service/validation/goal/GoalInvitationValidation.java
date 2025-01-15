package school.faang.user_service.validation.goal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.service.UserService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class GoalInvitationValidation {

    private static final int MAX_GOAL_USERS = 3;

    private final InvitationFilterDto filter;
    private final UserService userService;

    @Autowired
    public GoalInvitationValidation(InvitationFilterDto filter, UserService userService) {
        this.filter = filter;
        this.userService = userService;
    }

    public void checkAcceptingInvitation(Stream<GoalInvitationDto> allGoalInvitationDto, Long id){
        log.info("Проверка принятия приглашения");
        goalExistsOrNot(allGoalInvitationDto, id);
        invitedWorkingGoal(allGoalInvitationDto, id);
        allowedNumberActiveGoals(allGoalInvitationDto, id);
        log.info("Проверка принятия приглашения хавершена");
    }

    public void checkInvitation(GoalInvitationDto goalInvitationDto){
        log.info("Проверка приглашения");
        userVerificationForSendingInvitation(goalInvitationDto);
        checkAvailabilityUserInDatabase(goalInvitationDto);
        log.info("Проверка приглашения завершена");
    }

    public void checkRejectingInvitation(Stream<GoalInvitationDto> allGoalInvitationDto, Long id){
        log.info("Проверка отклонения");
        goalExistsOrNot(allGoalInvitationDto, id);
        log.info("Проверка отклонения завершена");
    }

    private void userVerificationForSendingInvitation(GoalInvitationDto goalInvitationDto){
        long inviterId = goalInvitationDto.getInviterId();
        long invitedId = goalInvitationDto.getInvitedUserId();

        if (inviterId == 0 || invitedId == 0 || inviterId == invitedId){
            String generalMessage = "В приглашении " + goalInvitationDto.getId() +
                    " неверно указан приглашающий и приглашенный пользователь";
            String exactMessage = "id inviter: " + inviterId + ", id invited: " + invitedId;
            log.warn(generalMessage);
            log.warn(exactMessage);
            throw new BusinessException(generalMessage);
        }
        log.info("Пользователи в приглашении прописаны верно");
    }

    private void checkAvailabilityUserInDatabase(GoalInvitationDto goalInvitationDto){
        long inviterId = goalInvitationDto.getInviterId();
        long invitedId = goalInvitationDto.getInvitedUserId();

        if (!userService.idVerificationUser(invitedId)){
            String message = "Пользователь c id" + invitedId + " не существует в базе";
            log.warn(message);
        };
        if (!userService.idVerificationUser(inviterId)){
            String message = "Пользователь c id" + inviterId + " не существует в базе";
            log.warn(message);
        }
        log.info("Пользователи присуствуют в базе");
    }

    private void allowedNumberActiveGoals(Stream<GoalInvitationDto> allGoalInvitationDto, Long id){
        Long userId = allGoalInvitationDto
                .collect(Collectors.toMap(GoalInvitationDto::getId, GoalInvitationDto::getInvitedUserId))
                .get(id);
        List<GoalInvitationDto> numberGoalUsers = allGoalInvitationDto
                .filter(goal -> goal.getInvitedUserId().equals(userId))
                .toList();
        if (numberGoalUsers.size() <= MAX_GOAL_USERS) {
            String message = "Приглашенный пользователь к заявке " + id + " подписан на максимальное количество целей";
            log.warn(message);
            throw new BusinessException(message);
        }
        log.info("!!!НЕ ЗАБЫТЬ ДОПИСАТЬ!!!");
    }

    private void invitedWorkingGoal(Stream<GoalInvitationDto> allGoalInvitationDto, Long id){
        GoalInvitationDto goalInvitationDto = allGoalInvitationDto
                .filter(goal -> goal.getId().equals(id))
                .findFirst().orElseThrow();
        if (goalInvitationDto.getStatus().equals(RequestStatus.PENDING)){
            String message = "Пользователь " + goalInvitationDto.getInvitedUserId() + " уже работает с целью id: " + id;
            log.warn(message);
            throw new BusinessException(message);
        }
        log.info("Пользователь {} еще не работает над целью с id: {}", goalInvitationDto.getInvitedUserId(), id);
    }

    private void goalExistsOrNot(Stream<GoalInvitationDto> allGoalInvitationDto, Long id){
        List<GoalInvitationDto> goalById = allGoalInvitationDto
                .filter(goal -> goal.getId().equals(id))
                .toList();
        if (goalById.isEmpty()){
            String message = "Цель с id: " + id + " не найдена";
            log.warn(message);
            throw new EntityNotFoundException(message);
        }
        log.info("Объект с id: {} найден в базе", id);
    }
}
