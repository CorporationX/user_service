package school.faang.user_service.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.filter.InvitationFilter;

import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class GoalInvitationService {

    GoalInvitationRepository goalInvitationRepository;
    GoalRepository goalRepository;
    UserRepository userRepository;
    GoalInvitationMapper goalInvitationMapper;
    List<InvitationFilter> invitationFilters;
    GoalInvitationServiceValidator goalInvitationServiceValidator;
    static final int SETGOAL_SIZE = 3;

    public GoalInvitationDto createInvitation(GoalInvitationDto goalInvitationDto) {
        goalInvitationServiceValidator.validateForCreateInvitation(goalInvitationDto);

        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(goalInvitationDto);
        goalInvitation.setInviter(userRepository.findById(goalInvitationDto.getInviterId()).get());
        goalInvitation.setInvited(userRepository.findById(goalInvitationDto.getInvitedUserId()).get());
        goalInvitation.setGoal(goalRepository.findById(goalInvitationDto.getGoalId()).get());

        goalInvitationRepository.save(goalInvitation);
        return goalInvitationMapper.toDto(goalInvitation);
    }

    public void acceptGoalInvitation(long id) {
        goalInvitationServiceValidator.validateForAcceptGoalInvitation(id);

        GoalInvitation goalInvitation = goalInvitationRepository.findById(id).get();
        goalInvitationRepository.findById(id).get().setStatus(RequestStatus.ACCEPTED);
        goalInvitation.getInvited().getGoals().add(goalRepository.findById(goalInvitation.getGoal().getId()).get());

        goalInvitationRepository.save(goalInvitation);
    }

    public void rejectGoalInvitation(long id) {
        goalInvitationServiceValidator.validateForRejectGoalInvitation(id);

        GoalInvitation goalInvitation = goalInvitationRepository.findById(id).get();
        goalRepository.findById(goalInvitation.getGoal().getId()).get();

        goalInvitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(goalInvitation);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filters) {
        goalInvitationServiceValidator.validateForGetInvitations(filters);

        return invitationFilters.stream().
                filter(filter -> filter.isApplicable(filters)).
                flatMap(filter -> filter.apply(goalInvitationRepository.findAll().stream(), filters)).
                distinct().
                map(goalInvitationMapper::toDto).
                toList();
    }
}
