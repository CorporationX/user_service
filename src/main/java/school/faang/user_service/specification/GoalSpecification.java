package school.faang.user_service.specification;

import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalStatusDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class GoalSpecification {

    public static final String TITLE_FIELD = "title";
    public static final String DESCRIPTION_FIELD = "description";
    public static final String STATUS_FIELD = "status";
    public static final String DEADLINE_FIELD = "deadline";
    public static final String SKILLS_JOIN = "skillsToAchieve";
    public static final String USERS_JOIN = "users";
    public static final String SKILL_ID_FIELD = "id";
    public static final String USER_ID_FIELD = "id";

    public static Specification<Goal> build(GoalFilterDto filter) {
        return Specification.where(hasTitle(filter.title()))
                .and(hasDescription(filter.description()))
                .and(hasStatus(filter.status()))
                .and(hasDeadline(filter.deadline()))
                .and(hasSkills(filter.skillIds()))
                .and(hasUsers(filter.userIds()));
    }

    private static Specification<Goal> hasTitle(String title) {
        return (root, query, cb) ->
                title == null ? cb.conjunction()
                        : cb.like(root.get(TITLE_FIELD), "%" + title + "%");
    }

    private static Specification<Goal> hasDescription(String description) {
        return (root, query, cb) ->
                description == null ? cb.conjunction()
                        : cb.like(root.get(DESCRIPTION_FIELD), "%" + description + "%");
    }

    private static Specification<Goal> hasStatus(GoalStatusDto status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction()
                        : cb.equal(root.get(STATUS_FIELD), GoalStatus.valueOf(status.name()));
    }

    private static Specification<Goal> hasDeadline(LocalDateTime deadline) {
        return (root, query, cb) ->
                deadline == null ? cb.conjunction()
                        : cb.lessThanOrEqualTo(root.get(DEADLINE_FIELD), deadline);
    }

    private static Specification<Goal> hasSkills(List<Long> skillIds) {
        return (root, query, cb) -> {
            if (skillIds == null || skillIds.isEmpty()) {
                return cb.conjunction();
            }
            Join<Goal, Long> skillJoin = root.join(SKILLS_JOIN);
            return skillJoin.get(SKILL_ID_FIELD).in(skillIds);
        };
    }

    private static Specification<Goal> hasUsers(List<Long> userIds) {
        return (root, query, cb) -> {
            if (userIds == null || userIds.isEmpty()) {
                return cb.conjunction();
            }
            Join<Goal, User> userJoin = root.join(USERS_JOIN);
            return userJoin.get(USER_ID_FIELD).in(userIds);
        };
    }
}
