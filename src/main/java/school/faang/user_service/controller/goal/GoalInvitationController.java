package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("${controller.goalInvitation.baseUrl}")
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;
    private final GoalInvitationMapper mapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<GoalInvitationDto> getInvitations(@RequestBody(required = false) InvitationFilterDto filter) {
        List<GoalInvitation> filteredInvitations = goalInvitationService.getInvitations(filter);
//        List<GoalInvitation> filteredInvitations = goalInvitationService.getInvitationsBySpec(filter);
        return mapper.toDtoList(filteredInvitations);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GoalInvitationDto createInvitation(@RequestBody @Validated GoalInvitationDto dto) {
        GoalInvitation savedInvitation = goalInvitationService.createInvitation(mapper.toEntity(dto));
        return mapper.toDto(savedInvitation);
    }

    @PatchMapping("/{id}/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acceptGoalInvitation(@PathVariable long id) {
        goalInvitationService.acceptGoalInvitation(id);
    }

    @PatchMapping("/{id}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectGoalInvitation(@PathVariable long id) {
        goalInvitationService.rejectGoalInvitation(id);
    }
}
