package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;

    public void createInvitation(GoalInvitationDto invitation) {
        if (invitation.getInviterId().equals(invitation.getInvitedUserId())) {
            throw new IllegalArgumentException("Приглашающий и приглашенный - один и тот же человек");
        }
        if (invitation.getInviterId() == null || invitation.getInvitedUserId() == null) {
            throw new IllegalArgumentException("Пользователь не существует");
        }
        goalInvitationRepository.save(invitation);
    }

    public void acceptGoalInvitation(long id){
        GoalInvitation invitation = goalInvitationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Приглашение не найдено."));
        List<GoalInvitation> listInvintations = invitation.getInvited().getReceivedGoalInvitations();
        if(listInvintations.size() > 2){
            throw new IllegalArgumentException("Нельзя иметь больше 3 активных целей.");
        }
        if(listInvintations.contains(id)){
            throw new IllegalArgumentException("У вас уже есть такая цель.");
        }
        listInvintations.add(invitation);
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
}


