package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.GoalInvitationException;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private  final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final UserMapper userMapper;

    private final GoalInvitationMapper goalInvitationMapper;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitationDto)
    {
        if(invitationDto.getInvitedUserId()<=0&&invitationDto.getInviterId()<=0) {
            throw new GoalInvitationException("InvationDto not found");
        }
            if (!goalInvitationRepository.existsById(invitationDto.getInviterId())
                    &&!goalInvitationRepository.existsById(invitationDto.getInvitedUserId()))
            {            throw new GoalInvitationException("InvationDto missing from the database");
            }
                GoalInvitation goalInvitation = goalInvitationMapper.toEntity(invitationDto);
                return goalInvitationMapper.toDto(goalInvitationRepository.save(goalInvitation));
        }


    public GoalInvitationDto acceptGoalInvitation(long id)
    {

            GoalInvitationDto  invitationDto =  goalInvitationMapper.toDto
                    ((goalInvitationRepository.findById(id)
                            .stream()
                            .findAny()
                            .get()));

           UserDto userDto=UserMapper.INSTANCE.userToDto
                   (userRepository.findById(invitationDto.getInvitedUserId())
                    .stream()
                    .findAny()
                    .get());

            GoalDto goalDto = GoalMapper.INSTANCE.toDto
                    (goalRepository.findById(invitationDto.getGoalId())
                    .stream()
                    .findAny()
                    .get());

        if(!userDto.getGoals().contains(goalDto)&&userDto.getGoals().size()>=3)
        {
            throw new GoalInvitationException("The user is already in the goal or he is already participating in three goals");
        }
                userDto.addGoals(goalDto);
                invitationDto.setStatus(RequestStatus.valueOf("ACCEPTED"));
                User user = userMapper.dtoToUser(userDto);
                GoalInvitation goalInvitation = goalInvitationMapper.toEntity(invitationDto);
                goalInvitationRepository.save(goalInvitation);
                userRepository.save(user);
                return invitationDto;
            }



    public GoalInvitationDto rejectGoalInvitation(long id)
    {
        if(!goalRepository.existsById(id))
        {
            throw new GoalInvitationException("target not found in database");
        }
        GoalInvitationDto  invitationDto =  goalInvitationMapper.toDto
                ((goalInvitationRepository.findById(id)
                        .stream()
                        .findAny()
                        .get()));

        invitationDto.setStatus(RequestStatus.valueOf("REJECTED"));

        GoalInvitation goalInvitation = GoalInvitationMapper.INSTANCE.toEntity(invitationDto);
        return goalInvitationMapper.toDto(goalInvitationRepository.save(goalInvitation));
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filter){

        return new ArrayList<>();
    }
}
