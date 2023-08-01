package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

@Service

@RequiredArgsConstructor
public class GoalInvitationService {
    private  final GoalInvitationRepository goalInvitationRepository;
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
            throw new DataValidationException("InvationDto " + invitationDto.getId() + " missing from the database");
        }
        throw new DataValidationException("InvationDto " + invitationDto.getId() + " not found");
    }


}
