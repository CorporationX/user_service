package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

@Tag(name = "goalInvitation_methods")
@Controller
@RequiredArgsConstructor
@Slf4j
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    @Operation(
            summary = "Создание приглашения на цель",
            description = "Создаёт приглашение на цель для пользователя."
    )
    public Long createInvitation(GoalInvitationDto invitationDto) {
        log.info("Creating goal invitation for: {}", invitationDto);
        Long id = goalInvitationService.createInvitation(invitationDto).getId();
        log.info("Goal invitation for: {} created successfully", invitationDto);
        return id;
    }

    @Operation(
            summary = "Принятие приглашения на цель",
            description = "Позволяет пользователю принять приглашение на цель."
    )
    public void acceptGoalInvitation(long id) {
        log.info("Accepting goal invitation with ID: {}", id);
        goalInvitationService.acceptGoalInvitation(id);
        log.info("Goal invitation with ID: {} accepted successfully", id);

    }

    @Operation(
            summary = "Отклонение приглашения на цель",
            description = "Позволяет пользователю отклонить приглашение на цель."
    )
    public void rejectGoalInvitation(long id) {
        log.info("Rejecting goal invitation with ID: {}", id);
        goalInvitationService.rejectGoalInvitation(id);
        log.info("Goal invitation with ID: {} rejected successfully", id);
    }

    @Operation(
            summary = "Получение списка приглашений на цель",
            description = "Получает список приглашений на цель с фильтрацией по заданным параметрам."
    )
    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filterDto) {
        log.info("Getting goal invitations with filter {}", filterDto);
        var goalInvitations = goalInvitationService.getInvitations(filterDto);
        log.info("Goal invitations with filter {} received successfully", filterDto);
        return goalInvitations;
    }
}
