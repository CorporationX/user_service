package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.FilteredResponse;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;
import school.faang.user_service.util.goal.exception.UseFiltersException;
import school.faang.user_service.util.goal.validator.GoalInvitationControllerValidator;

@RestController
@RequiredArgsConstructor
@RequestMapping("/goal/invitation")
public class GoalInvitationController {

    private final GoalInvitationControllerValidator goalInvitationValidator;
    private final GoalInvitationService goalInvitationService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public GoalInvitationDto createInvitation(@Valid @RequestBody GoalInvitationDto goalInvitationDto) {
        goalInvitationValidator.checkInviterAndInvitedAreTheSame(goalInvitationDto);

        return goalInvitationService.createInvitation(goalInvitationDto);
    }

//    @PutMapping("/accept/{id}")
//    public ResponseEntity<?> acceptGoalInvitation(@PathVariable Long id) {
//        goalInvitationValidator.validateInvitation(id);
//
//        return ResponseEntity.ok(goalInvitationService.acceptGoalInvitation(id));
//    }
//
//    @PutMapping("/reject/{id}")
//    public ResponseEntity<?> rejectGoalInvitation(@PathVariable Long id) {
//        goalInvitationValidator.validateInvitation(id);
//
//        return ResponseEntity.ok(goalInvitationService.rejectGoalInvitation(id));
//    }
//
//    @GetMapping("/get")
//    public ResponseEntity<FilteredResponse> getInvitations(@RequestBody InvitationFilterDto invitationFilterDto) {
//        goalInvitationValidator.validateInvitation(invitationFilterDto, new UseFiltersException());
//
//        return ResponseEntity.ok(new FilteredResponse(goalInvitationService.getInvitations(invitationFilterDto)));
//    }
}
