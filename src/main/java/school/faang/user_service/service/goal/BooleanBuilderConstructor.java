package school.faang.user_service.service.goal;

import com.querydsl.core.BooleanBuilder;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.QGoalInvitation;

@Component
public class BooleanBuilderConstructor {
    public BooleanBuilder getQueryBooleanBuilder(InvitationFilterDto invitationFilterDto) {
        QGoalInvitation qGoalInvitation = QGoalInvitation.goalInvitation;
        BooleanBuilder builder = new BooleanBuilder();
        if (invitationFilterDto.getInviterId() != null) {
            builder.and(qGoalInvitation.inviter.id.eq(invitationFilterDto.getInviterId()));
        }
        if (invitationFilterDto.getInvitedId() != null) {
            builder.and(qGoalInvitation.invited.id.eq(invitationFilterDto.getInvitedId()));
        }
        if (invitationFilterDto.getStatus() != null) {
            builder.and(qGoalInvitation.status.eq(invitationFilterDto.getStatus()));
        }
        if (invitationFilterDto.getCreatedBefore() != null) {
            builder.and(qGoalInvitation.createdAt.loe(invitationFilterDto.getCreatedBefore()));
        }
        if (invitationFilterDto.getCreatedAfter() != null) {
            builder.and(qGoalInvitation.createdAt.goe(invitationFilterDto.getCreatedAfter()));
        }
        return builder;
    }
}
