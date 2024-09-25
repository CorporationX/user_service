package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/goal")
@RequiredArgsConstructor
public class GoalInvitationController {

    private static final String X_USER_ID = "x-user-id";

    private final GoalInvitationService goalInvitationService;

    @PostMapping(value = "/invitation")
    public ResponseEntity<GoalInvitationDto> createInvitation(@RequestHeader(X_USER_ID) Long inviterId,
                                                           @Valid @RequestBody GoalInvitationDto invitation) {
        log.info("POST /invitation  inviterId:{}", inviterId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(goalInvitationService.createInvitation(inviterId, invitation));
    }

    @PatchMapping(value = "/accept")
    public ResponseEntity<GoalInvitationDto> acceptGoalInvitation(@RequestHeader(X_USER_ID) Long invitedId,
                                                                  @RequestBody Long goalInvitationId) {
        log.info("PATCH /accept  invitedId:{}", invitedId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(goalInvitationService.acceptGoalInvitation(goalInvitationId, invitedId));
    }

    @PatchMapping(value = "/reject")
    public ResponseEntity<GoalInvitationDto> rejectGoalInvitation(@RequestHeader(X_USER_ID) Long invitedId,
                                                                  @RequestBody Long goalInvitationId) {
        log.info("PATCH /goal/reject  invitedId:{}", invitedId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(goalInvitationService.rejectGoalInvitation(goalInvitationId, invitedId));
    }

    @GetMapping(value = "/invitations")
    public ResponseEntity<List<GoalInvitationDto>> getInvitations(@RequestBody InvitationFilterDto filter,
                                                                  @RequestParam(defaultValue = "0") Integer from,
                                                                  @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /invitations");
        return ResponseEntity.status(HttpStatus.OK).body(goalInvitationService.getInvitations(filter, from, size));
    }
}