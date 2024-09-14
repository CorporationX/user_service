package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.service.GoalInvitationService;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/goal-invitations")
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;
    private final GoalInvitationMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public List<GoalInvitationDto> getInvitations(@RequestBody(required = false) InvitationFilterDto filter) {
        List<GoalInvitation> filteredInvitations = goalInvitationService.getInvitations(filter);
        return mapper.toDtoList(filteredInvitations);
    }

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public GoalInvitationDto createInvitation(@RequestBody @Valid GoalInvitationDto dto) {
        GoalInvitation toSave = mapper.toEntity(dto);
        System.out.println(toSave);
        GoalInvitation savedInvitation = goalInvitationService.createInvitation(toSave);
        return mapper.toDto(savedInvitation);
    }

    @PutMapping("/{id}/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acceptGoalInvitation(@PathVariable long id) {
        goalInvitationService.acceptGoalInvitation(id);
    }

    @PutMapping("/{id}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectGoalInvitation(@PathVariable long id) {
        goalInvitationService.rejectGoalInvitation(id);
    }
}
