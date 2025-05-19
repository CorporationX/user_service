package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.dto.goal.SortOption;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.goal.GoalInvitationServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/goals/invitations")
public class GoalInvitationController {
    private final GoalInvitationServiceImpl goalInvitationServiceImpl;

    @PostMapping
    public ResponseEntity<GoalInvitationDto> createInvitation(@RequestBody @Valid GoalInvitationDto goalInvitationDto) {
        log.info("Request received: method=POST, URI=/goals/invitations, goalInvitationDto={}", goalInvitationDto);
        return new ResponseEntity<>(goalInvitationServiceImpl.createInvitation(goalInvitationDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<Void> acceptGoalInvitation(@PathVariable(name = "id") @NotNull Long id) {
        log.info("Request received: method=Patch, URI=/goals/invitations/accept/{id}, id={}", id);
        goalInvitationServiceImpl.acceptGoalInvitation(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<Void> rejectGoalInvitation(@PathVariable(name = "id") @NotNull Long id) {
        log.info("Request received: method=Patch, URI=/goals/invitations/reject/{id}, id={}", id);
        goalInvitationServiceImpl.rejectGoalInvitation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<GoalInvitationDto>> getAllInvitations(@RequestParam(name = "inviter_id", required = false)
                                                                     @Min(value = 1, message = "invitedId value cannot be lower than 1")
                                                                     Long inviterId,
                                                                     @RequestParam(name = "invited_id", required = false)
                                                                     @Min(value = 1, message = "invitedId value cannot be lower than 1")
                                                                     Long invitedId,
                                                                     @RequestParam(name = "status", required = false)
                                                                     RequestStatus status,
                                                                     @RequestParam(name = "created_before", required = false)
                                                                     @DateTimeFormat(pattern = "yyyy-MM-dd-HH-mm-ss")
                                                                     LocalDateTime createdBefore,
                                                                     @RequestParam(name = "created_after", required = false)
                                                                     @DateTimeFormat(pattern = "yyyy-MM-dd-HH-mm-ss")
                                                                     LocalDateTime createdAfter,
                                                                     @RequestParam(name = "offset", defaultValue = "0")
                                                                     @Min(value = 0)
                                                                     Integer offset,
                                                                     @RequestParam(name = "size", defaultValue = "10")
                                                                     @Min(value = 1) @Max(value = 10)
                                                                     Integer size,
                                                                     @RequestParam(name = "sort", required = false)
                                                                     SortOption sort) {
        log.info("Request received: method=GET, URI=/goals/invitations. Параметры inviterId={}, invitedId={}, " +
                        "status={}, createdBefore={}, createdAfter={}, offset={}, size={}", inviterId, invitedId,
                status, createdBefore, createdAfter, offset, size);
        InvitationFilterDto invitationFilterDto = new InvitationFilterDto(inviterId, invitedId, status, createdBefore,
                createdAfter, offset, size, sort);
        return new ResponseEntity<>(goalInvitationServiceImpl.getAllInvitations(invitationFilterDto), HttpStatus.OK);
    }
}
