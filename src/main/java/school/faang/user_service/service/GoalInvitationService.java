package school.faang.user_service.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    public GoalInvitationDto createInvitation(@NotNull GoalInvitationDto goalInvitationDto) {

        if(goalInvitationDto.getInvitedUserId().equals(goalInvitationDto.getInviterId())){
            throw new IllegalArgumentException("Exception invited user can`t be invitor ");
        }
        if(!userRepository.existsById(goalInvitationDto.getInviterId())){
            throw new NoSuchElementException("User with id:"+goalInvitationDto.getInviterId()+" doesn't exist!");
        }
        if(!userRepository.existsById(goalInvitationDto.getInvitedUserId())){
            throw new NoSuchElementException("User with id:"+goalInvitationDto.getInvitedUserId()+" doesn't exist!");
        }
        if(!goalRepository.existsById(goalInvitationDto.getGoalId())){
            throw new NoSuchElementException("User with id:"+goalInvitationDto.getInvitedUserId()+" doesn't exist!");
        }
        GoalInvitation savedInvitation = goalInvitationRepository.save(goalInvitationMapper.toEntity(goalInvitationDto));

        return goalInvitationMapper.toDto(savedInvitation);
    }
    public GoalInvitationDto acceptGoalInvitation(long id){
        GoalInvitation savedInvitation = goalInvitationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No such goal invitation with id:"+id));

        User invited = savedInvitation.getInvited();

        if(invited.getReceivedGoalInvitations().size()>3)
            throw new IllegalArgumentException("Exception invited user can`t have more than 3 goal invitations");

        savedInvitation.setStatus(RequestStatus.ACCEPTED);
        goalInvitationRepository.save(savedInvitation);
        return goalInvitationMapper.toDto(savedInvitation);
    }
}
