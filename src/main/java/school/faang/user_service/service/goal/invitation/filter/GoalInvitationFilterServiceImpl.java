package school.faang.user_service.service.goal.invitation.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filter.goal.GoalInvitationFilter;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalInvitationFilterServiceImpl implements GoalInvitationFilterService {

    private final List<GoalInvitationFilter> filters;

    @Override
    public Stream<GoalInvitation> applyFilters(Stream<GoalInvitation> goalInvitations, InvitationFilterDto filterDto) {
        if (filterDto != null) {
            for (GoalInvitationFilter filter : filters) {
                if (filter.isAcceptable(filterDto)) {
                    goalInvitations = filter.applyFilter(goalInvitations, filterDto);
                }
            }
        }

        return goalInvitations;
    }
}
