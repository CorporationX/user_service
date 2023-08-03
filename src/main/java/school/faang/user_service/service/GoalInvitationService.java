package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.GoalInvitationException;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

@Service

@RequiredArgsConstructor
public class GoalInvitationService {
    private  final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    //private final GoalInvitationMapper goalInvitationMapper;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitationDto)
    {
        if(invitationDto.getInvitedUserId()!=0&&invitationDto.getInviterId()!=0)
        {
            if (!goalInvitationRepository.existsById(invitationDto.getInviterId())
                    &&!goalInvitationRepository.existsById(invitationDto.getInvitedUserId()))
            {
                GoalInvitation goalInvitation = GoalInvitationMapper.INSTANCE.toEntity(invitationDto);
                goalInvitationRepository.save(goalInvitation);
                return GoalInvitationMapper.INSTANCE.toDto(goalInvitation);
            }
            throw new GoalInvitationException("InvationDto " + invitationDto.getId() + " missing from the database");
        }
        throw new GoalInvitationException("InvationDto " + invitationDto.getId() + " not found");
    }

    public void acceptGoalInvitation(long id)
    {
            GoalInvitationDto  invitationDto =  GoalInvitationMapper.INSTANCE.toDto
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

            if(userDto.getGoals().contains(goalDto)&&userDto.getGoals().size()<3)
            {
                userDto.addGoals(goalDto);
                invitationDto.setStatus(RequestStatus.valueOf("ACCEPTED"));
                User user = UserMapper.INSTANCE.dtoToUser(userDto);
                GoalInvitation goalInvitation = GoalInvitationMapper.INSTANCE.toEntity(invitationDto);
                goalInvitationRepository.save(goalInvitation);
                userRepository.save(user);
            }

        throw new DataValidationException("The user is already in the goal or he is already participating in three goals");
    }
}
