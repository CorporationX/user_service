package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.GoalInvitationService;
import school.faang.user_service.service.GoalService;
import school.faang.user_service.service.UserService;

@Service
@RequiredArgsConstructor
public class GoalInvitationServiceImpl implements GoalInvitationService {

    private final ApplicationContext context;

    private final GoalInvitationRepository goalInvitationRepository;

    private final GoalInvitationMapper goalInvitationMapper;

    @Override
    public GoalInvitation createInvitation(GoalInvitationDto goalInvitationDto) {
        Long inviterId = goalInvitationDto.getInviterId();
        Long invitedUserId = goalInvitationDto.getInvitedUserId();
        if (invitedUserId.equals(inviterId))
            throw new IllegalArgumentException("Inviter and Invited IDs are the same ");

        //коммент для ревьювера: проверка на наличие юзеров в бд
        // происходит в user service, который вызывается используется в маппинге.
        GoalInvitation goalInvitation = goalInvitationMapper.gIDTOToGoalInvitation(
                goalInvitationDto,
                context.getBean(GoalService.class),
                context.getBean(UserService.class)
        );

        return goalInvitationRepository.saveAndFlush(goalInvitation);
    }
}
