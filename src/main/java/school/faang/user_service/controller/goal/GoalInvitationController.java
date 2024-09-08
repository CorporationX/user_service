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
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("goal/invitations")
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;
    private final GoalInvitationMapper goalInvitationMapper;

    @PostMapping
    public ResponseEntity<GoalInvitationDto> createInvitation(@Valid @RequestBody GoalInvitationDto invitationDto) {
        var goalInvitation = goalInvitationService.createInvitation(goalInvitationMapper.toEntity(invitationDto),
                invitationDto.inviterId(), invitationDto.invitedUserId(), invitationDto.goalId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(goalInvitationMapper.toDto(goalInvitation));
    }

    @PutMapping("accept/{id}")
    public ResponseEntity<Void> acceptGoalInvitation(@PathVariable long id) {
        goalInvitationService.acceptGoalInvitation(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("reject/{id}")
    public ResponseEntity<Void> rejectGoalInvitation(@PathVariable long id) {
        goalInvitationService.rejectGoalInvitation(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity<List<GoalInvitationDto>> getInvitations(@RequestBody InvitationFilterDto filter) {
        var invitationDtoList = goalInvitationService.getInvitations(filter)
                .stream()
                .map(goalInvitationMapper::toDto)
                .toList();
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(invitationDtoList);
    }
}
