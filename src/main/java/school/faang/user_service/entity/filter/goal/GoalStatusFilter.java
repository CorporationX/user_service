package school.faang.user_service.entity.filter.goal;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.FilterGoalDto;
import school.faang.user_service.entity.goal.Goal;

@Component
public class GoalStatusFilter implements GoalFilterInterface {
    @Override
    public boolean isApplicable(FilterGoalDto dto) {
        return dto.status() != null;
    }

    @Override
    public Specification<Goal> apply(Specification<Goal> specification, FilterGoalDto dto) {
        return specification.and((root, query, cb) ->
                cb.equal(root.get("status"), dto.status())
        );
    }
}
