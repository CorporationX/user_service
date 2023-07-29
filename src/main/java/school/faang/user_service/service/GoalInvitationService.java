package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.filter.goal.InvitationFilter;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final List<InvitationFilter> filters;

    @Transactional
    public GoalInvitationDto createInvitation(GoalInvitationDto invitationDto) {
        validateInvitation(invitationDto);
        GoalInvitation invitation = goalInvitationRepository.save(goalInvitationMapper.toEntity(invitationDto));
        return goalInvitationMapper.toDto(invitation);
    }

    @Transactional(readOnly = true)
    public List<GoalInvitationDto> getInvitations(GoalInvitationFilterDto filter) {
        Stream<GoalInvitation> goalInvitationStream = goalInvitationRepository.findAll().stream();

        for (InvitationFilter invitationFilter : filters) {
            if (invitationFilter.isApplicable(filter)) {
                goalInvitationStream = invitationFilter.apply(goalInvitationStream, filter);
            }
        }

        return goalInvitationMapper.toDtoList(goalInvitationStream.toList());
    }

    @Transactional(readOnly = true)
    private void validateInvitation(GoalInvitationDto invitation) {
        if (invitation.getId() == null || invitation.getId() < 1) {
            throw new DataValidException("Invitation illegal id");
        }
        if (!goalRepository.existsById(invitation.getGoalId())) {
            throw new DataValidException("Goal does not exist. Invitation Id: " + invitation.getId());
        }
        if (!userRepository.existsById(invitation.getInviterId())) {
            throw new DataValidException("Inviter does not exist, Id: " + invitation.getInviterId());
        }
        if (!userRepository.existsById(invitation.getInvitedUserId())) {
            throw new DataValidException("Invited does not exist, Id: " + invitation.getInvitedUserId());
        }
        if (invitation.getInviterId().equals(invitation.getInvitedUserId())) {
            throw new DataValidException("Inviter and invited are equal. Invitation Id: " + invitation.getId());
        }
    }
}
