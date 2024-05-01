package school.faang.user_service.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GoalInvitationService {
    GoalInvitationRepository goalInvitationRepository;
    GoalInvitationMapper goalInvitationMapper;

    public void createInvitation(GoalInvitationDto goalInvitationDto) {
        if (goalInvitationDto.getInviterId() != null && goalInvitationDto.getInvitedUserId() != null) {
            if (!goalInvitationDto.getInviterId().equals(goalInvitationDto.getInvitedUserId())) {
                if (goalInvitationRepository.existsById(goalInvitationDto.getInviterId()) && goalInvitationRepository.existsById(goalInvitationDto.getInvitedUserId())) {
                    goalInvitationRepository.save(goalInvitationMapper.toEntity(goalInvitationDto));
                } else {
                    throw new RuntimeException("There is no such user in database");
                }
            } else {
                throw new RuntimeException("InviterId equals InvitedUserId");
            }
        } else {
            throw new RuntimeException("Input is null");
        }
    }
}
