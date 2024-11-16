package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoalInvitationDto;
import school.faang.user_service.dto.GoalInvitationResponseDto;
import school.faang.user_service.filter.GoalInvitationFilter;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.mapper.GoalInvitationResponseMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final GoalInvitationResponseMapper goalInvitationResponseMapper;
    private final List<GoalInvitationFilter> goalnvitationFilterList;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitationDto) {
        createInvitationValidation(invitationDto);
        GoalInvitation invitation = goalInvitationMapper.toEntity(invitationDto);
        goalInvitationRepository.save(invitation);
        return goalInvitationMapper.toInvitationDTO(invitation);
    }

    public GoalInvitationResponseDto acceptGoalInvitation(long id) {
        GoalInvitation invitation = goalInvitationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invitation not found."));
        List<GoalInvitation> listInvitations = invitation.getInvited().getReceivedGoalInvitations();
        if (listInvitations.size() > 2) {
            throw new IllegalArgumentException("You cannot have more than 3 active goals.");
        }
        if (listInvitations.contains(id)) {
            throw new IllegalArgumentException("You already have such a goal.");
        }
        listInvitations.add(invitation);
        return goalInvitationResponseMapper.toInvitationDTO(invitation);
    }

    public boolean rejectGoalInvitation(long id) {
        if (goalInvitationRepository.existsById(id)) {
            goalInvitationRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public List<GoalInvitation> getInvitationsByFilter(GoalInvitationFilterDto goalInvitationFilterDto) {
        Stream<GoalInvitation> allVacancy = goalInvitationRepository.findAll().stream();
        List<GoalInvitation> invitationList = goalnvitationFilterList.stream()
                .filter(filter -> filter.isApplicable(goalInvitationFilterDto))
                .flatMap(filter -> filter.apply(allVacancy, goalInvitationFilterDto)).collect(Collectors.toList());
        return invitationList;

    }

    public void createInvitationValidation(GoalInvitationDto invitationDTO) {
        if (invitationDTO.getInviterId().equals(invitationDTO.getInvitedUserId())) {
            throw new IllegalArgumentException("The inviter and the invitee are the same person.");
        }
        if (invitationDTO.getInviterId() == null || invitationDTO.getInvitedUserId() == null) {
            throw new IllegalArgumentException("User does not exist.");
        }
    }
}


