package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.service.goal.GoalInvitationService;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/goal-invitations")
@Tag(name = "Goal Invitation Management", description = "Operations related to inviting other users to work on goals together")
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;
    private final GoalInvitationMapper mapper;

    @Operation(summary = "Get filtered invitations", description = "Returns goal invitations, based on a provided filter object")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Successfully retrieved filtered invitations"))
    @PostMapping
    public List<GoalInvitationDto> getInvitations(@Parameter(description = "Optional filtering criteria for invitations")
                                                  @RequestBody(required = false) InvitationFilterDto filter) {
        List<GoalInvitation> filteredInvitations = goalInvitationService.getInvitations(filter);
        return mapper.toDtoList(filteredInvitations);
    }

    @Operation(summary = "Invite a user to work on goal", description = "Creates a new goal invitation in the system, to work on a goal with another user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created"),
            @ApiResponse(responseCode = "400", description = """
            Can't create an invitation. Will happen if:
            - an ID of non-existing inviting user was provided
            - an ID of non-existing invited user was provided
            - an ID of non-existing goal was provided
            - IDs of inviting and invited user are the same""")})
    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public GoalInvitationDto createInvitation(@RequestBody @Valid GoalInvitationDto dto) {
        GoalInvitation toSave = mapper.toEntity(dto);
        GoalInvitation savedInvitation = goalInvitationService.createInvitation(toSave);
        return mapper.toDto(savedInvitation);
    }

    @Operation(summary = "Accept an invitation", description = "Rejects a received goal invitation, sent by another user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully accepted"),
            @ApiResponse(responseCode = "400", description = """
            Can't accept an invitation. Will happen if:
            - an ID of non-existing invitation was provided
            - invitation refers to a goal with non-existing ID
            - an invited user in is already working on a goal, related to this invitation
            - an invited user has reached MAX active goals""")})
    @PutMapping("/{id}/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acceptGoalInvitation(@Parameter(description = "ID of an invitation to accept", required = true)
                                     @PathVariable long id) {
        goalInvitationService.acceptGoalInvitation(id);
    }

    @Operation(summary = "Reject an invitation", description = "Rejects a received goal invitation, sent by another user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully rejected"),
            @ApiResponse(responseCode = "400", description = """
            Can't reject an invitation. Will happen if:
            - an ID of non-existing invitation was provided
            - invitation refers to a goal with non-existing ID""")})
    @PutMapping("/{id}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectGoalInvitation(@Parameter(description = "ID of an invitation to reject", required = true)
                                     @PathVariable long id) {
        goalInvitationService.rejectGoalInvitation(id);
    }
}
