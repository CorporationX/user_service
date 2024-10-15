package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoalInvitationDto;
import school.faang.user_service.filter.GoalInvitationFilter;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
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
    private final List<GoalInvitationFilter> goalnvitationFilterList;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitationDTO) {
        createInvitationValidation(invitationDTO);
        GoalInvitation invitation = goalInvitationMapper.toEntity(invitationDTO);
        goalInvitationRepository.save(invitation);
        return goalInvitationMapper.toInvitationDTO(invitation);
    }

    public void acceptGoalInvitation(long id) {
        GoalInvitation invitation = goalInvitationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Приглашение не найдено."));
        List<GoalInvitation> listInvitations = invitation.getInvited().getReceivedGoalInvitations();
        if (listInvitations.size() > 2) {
            throw new IllegalArgumentException("Нельзя иметь больше 3 активных целей.");
        }
        if (listInvitations.contains(id)) {
            throw new IllegalArgumentException("У вас уже есть такая цель.");
        }
        listInvitations.add(invitation);
    }

    public void rejectGoalInvitation(long id) {
        if (goalInvitationRepository.existsById(id)) {
            goalInvitationRepository.deleteById(id);
        } else throw new IllegalArgumentException("Приглашение отклонено.");
    }

    public List<GoalInvitation> getInvitationsByFilter(GoalInvitationFilterDto goalInvitationFilterDto) {
        Stream<GoalInvitation> allVacancy = goalInvitationRepository.findAll().stream();
        List<GoalInvitation> invitationList = goalnvitationFilterList.stream().filter(filter -> filter.isApplicable(goalInvitationFilterDto))
                .flatMap(filter -> filter.apply(allVacancy, goalInvitationFilterDto)).collect(Collectors.toList());
        return invitationList;

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


