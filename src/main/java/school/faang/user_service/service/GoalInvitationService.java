package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.GoalInvitationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.StreamSupport;

@Service

@RequiredArgsConstructor
public class GoalInvitationService {
    private  final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final GoalInvitationMapper goalInvitationMapper;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitationDto)
    {
        if(invitationDto.getInvitedUserId()!=0&&invitationDto.getInviterId()!=0)
        {
            if (!goalInvitationRepository.existsById(invitationDto.getInviterId())
                    &&!goalInvitationRepository.existsById(invitationDto.getInvitedUserId()))
            {
                GoalInvitation goalInvitation = goalInvitationMapper.toEntity(invitationDto);
                goalInvitationRepository.save(goalInvitation);
                return goalInvitationMapper.toDto(goalInvitation);
            }
            throw new GoalInvitationException("InvationDto " + invitationDto.getId() + " missing from the database");
        }
        throw new GoalInvitationException("InvationDto " + invitationDto.getId() + " not found");
    }

    public GoalInvitationDto acceptGoalInvitation(long id)
    {
        //GoalInvitationDto invitationDto;
        UserDto userDto = new UserDto();
        User userEntity = UserMapper.INSTANCE.dtoToUser(userDto);
        if(goalInvitationRepository.findById(id).isPresent())
        {
            GoalInvitationDto  invitationDto =  goalInvitationMapper.toDto
                    (goalInvitationRepository.findById(id)
                            .stream()
                            .findAny()
                            .get());


        //if(!userEntity.getGoals().contains(invitationDto.getGoalId())&&userEntity.getGoals().size()<3)
        //{
            userRepository.findById(invitationDto.getInvitedUserId()).stream()
                    .findAny()
                    .get()
                    .getGoals()
                    .add(goalRepository.findById(invitationDto.getGoalId()).get());
            invitationDto.setStatus(RequestStatus.valueOf("ACCEPTED"));
            return invitationDto;
       }
        throw new DataValidationException("The user is already in the goal or he is already participating in three goals" +"\n"
               +"Your target number: "+"invitationDto.getId() "+ "\n"
               +"Number of user goals: "+userEntity.getGoals().size());
    }
}
