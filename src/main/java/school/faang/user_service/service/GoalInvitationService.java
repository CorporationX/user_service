package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;

    public  GoalInvitationDto createInvitation(GoalInvitationDto invitationDTO) {
        createInvitationValidation(invitationDTO);
        GoalInvitation invitation = goalInvitationMapper.toEntity(invitationDTO);
        goalInvitationRepository.save(invitation);
        return invitationDTO;
    }

    public void acceptGoalInvitation(long id){
        GoalInvitation invitation = goalInvitationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Приглашение не найдено."));
        List<GoalInvitation> listInvitations = invitation.getInvited().getReceivedGoalInvitations();
        if(listInvitations.size() > 2){
            throw new IllegalArgumentException("Нельзя иметь больше 3 активных целей.");
        }
        if(listInvitations.contains(id)){
            throw new IllegalArgumentException("У вас уже есть такая цель.");
        }
        listInvitations.add(invitation);
    }

    public void rejectGoalInvitation(long id){
        if(goalInvitationRepository.existsById(id)){
            goalInvitationRepository.deleteById(id);
        } else throw new IllegalArgumentException("Приглашение отклонено.");
    }

    public List<GoalInvitation>  getInvitations(InvitationFilterDto filter){
        return goalInvitationRepository.findAll().stream()
                .filter(invitation -> filter.getInviterNamePattern() == null ||
                        invitation.getInviter().getUsername().contains(filter.getInviterNamePattern()))
                .filter(invitation -> filter.getInvitedNamePattern() == null ||
                        invitation.getInvited().getUsername().contains(filter.getInvitedNamePattern()))
                .filter(invitation -> filter.getInviterId() == null ||
                        invitation.getInviter().getId().equals(filter.getInviterId()))
                .filter(invitation -> filter.getInvitedId() == null ||
                        invitation.getInvited().getId().equals(filter.getInvitedId()))
                .filter(invitation -> filter.getStatus() == null ||
                        invitation.getStatus().equals(filter.getStatus()))
                .collect(Collectors.toList());
    }

    public void createInvitationValidation(GoalInvitationDto invitationDTO) {
        if (invitationDTO.getInviterId().equals(invitationDTO.getInvitedUserId())) {
            throw new IllegalArgumentException("Приглашающий и приглашенный - один и тот же человек");
        }
        if (invitationDTO.getInviterId() == null || invitationDTO.getInvitedUserId() == null) {
            throw new IllegalArgumentException("Пользователь не существует");
        }
    }
}


