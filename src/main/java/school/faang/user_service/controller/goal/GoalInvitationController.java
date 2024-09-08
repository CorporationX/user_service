package school.faang.user_service.controller.goal;

import com.amazonaws.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.service.GoalInvitationService;
import school.faang.user_service.validator.goal.GoalInvitationValidator;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/goal-invitations")
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;
    private final GoalInvitationMapper mapper;
    private final GoalInvitationValidator validator;

    @GetMapping
    public ResponseEntity<List<GoalInvitationDto>> getInvitations(@RequestBody(required = false) InvitationFilterDto filter) {
        List<GoalInvitation> filteredInvitations = goalInvitationService.getInvitations(filter);

        return ResponseEntity.ok(
                mapper.toDtoList(filteredInvitations)
        );
    }

    @PostMapping
    public ResponseEntity<GoalInvitationDto> createInvitation(@RequestBody @Validated GoalInvitationDto dto) {
        validator.validateNewInvitation(dto);
        GoalInvitation savedInvitation = goalInvitationService.createInvitation(mapper.toEntity(dto));

        return new ResponseEntity<>(
                mapper.toDto(savedInvitation),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<GoalInvitationDto> acceptGoalInvitation(@PathVariable long id) {
        GoalInvitation validated = validator.validatedForAccepting(id);
        goalInvitationService.acceptGoalInvitation(validated);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<GoalInvitationDto> rejectGoalInvitation(@PathVariable long id) {
        GoalInvitation validated = validator.validatedForRejecting(id);
        goalInvitationService.rejectGoalInvitation(validated);

        return ResponseEntity.noContent().build();
    }
}
