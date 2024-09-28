package school.faang.user_service.filter;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.QGoalInvitation;

import java.util.Optional;

@Component
public class InvitationStatusFilterInvitation implements FilterInvitation<InvitationFilterDto, QGoalInvitation> {

    @Override
    public Optional<BooleanExpression> getCondition(InvitationFilterDto filter, QGoalInvitation qGoalInvitation) {
        if (filter.getStatus() != null) {
            return Optional.of(qGoalInvitation.status.eq(filter.getStatus()));
        }
        return Optional.empty();
    }
}