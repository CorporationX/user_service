package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.filter.goal.InvitationFilter;
import school.faang.user_service.validator.GoalValidator;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository invitationRepository;
    private final GoalMapper mapper;
    private final GoalValidator validator;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final List<InvitationFilter> invitationFilters;

    public GoalInvitationDto creatInvitation(GoalInvitationDto invitationDto) {
        validator.validateCorrectnessPlayers(invitationDto);

        GoalInvitation invitation = mapper.dtoToEntity(invitationDto);
        invitation.setInviter(userRepository.findById(invitationDto.getInviterId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден")));
        invitation.setInvited(userRepository.findById(invitationDto.getInvitedUserId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден")));
        invitation.setGoal(goalRepository.findById(invitationDto.getGoalId())
                .orElseThrow(() -> new IllegalArgumentException("Цель не найдена")));
        invitation.setStatus(RequestStatus.PENDING);

        invitation = invitationRepository.save(invitation);
        return mapper.entityToDto(invitation);
    }

    public GoalInvitationDto acceptGoalInvitation(long id) {
        GoalInvitation goalInvitation = invitationRepository.getReferenceById(id);
        validator.validateUsersAndGoals(goalInvitation);

        goalInvitation.setStatus(RequestStatus.ACCEPTED);

        return mapper.entityToDto(invitationRepository.save(goalInvitation));
    }

    public GoalInvitationDto rejectGoalInvitation(long id) {
        validator.validateExistGoal(id);

        GoalInvitation goalInvitation = invitationRepository.getReferenceById(id);
        goalInvitation.setStatus(RequestStatus.REJECTED);

        return mapper.entityToDto(invitationRepository.save(goalInvitation));
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filterDto) {
        List<GoalInvitation> invitations = invitationRepository.findAll();
        return invitationFilters.stream()
                .filter(f -> f.isApplicable(filterDto))
                .reduce(invitations.stream(), (stream, filter) -> filter
                        .apply(stream, filterDto), (s1, s2) -> s1)
                .map(mapper::entityToDto)
                .toList();
    }
}
