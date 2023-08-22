package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.GoalInvitationException;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

@Service

@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private static final int maxGoals = 3;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitationDto) {
        if (invitationDto.getInvitedUserId() <= 0 && invitationDto.getInviterId() <= 0) {
            throw new GoalInvitationException("InvationDto not found");
        }
        if (!goalInvitationRepository.existsById(invitationDto.getInviterId())
                && !goalInvitationRepository.existsById(invitationDto.getInvitedUserId())) {
            throw new GoalInvitationException("InvationDto missing from the database");
        }
        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(invitationDto);
        return goalInvitationMapper.toDto(goalInvitationRepository.save(goalInvitation));
    }

    public GoalInvitationDto acceptGoalInvitation(long id) {

        long idUser = goalInvitationRepository.findById(id).get().getInvited().getId();

        boolean haveTask = userRepository.findById(idUser).get().getGoals().contains(goalRepository.findById(id).get());

        boolean sizeUserGoals = userRepository.findById(idUser).get().getGoals().size()>=maxGoals;

        if (!haveTask || sizeUserGoals) {
            throw new GoalInvitationException("The user is already in the goal or he is already participating in three goals");
        }

        goalInvitationRepository.findById(id).get().setStatus(RequestStatus.valueOf("ACCEPTED"));

        GoalInvitationDto invitationDto = goalInvitationMapper.toDto((goalInvitationRepository.findById(id).stream().findAny().get()));

        return invitationDto;
    }
}
