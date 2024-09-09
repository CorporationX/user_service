package school.faang.user_service.repository.goal.specification;

import org.springframework.data.jpa.domain.Specification;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.Objects;

public class GoalInvitationSpecification {
    public static Specification<GoalInvitation> hasInviterId(Long inviterId) {
        return (root, query, cb) -> Objects.isNull(inviterId)
                ? cb.conjunction()
                : cb.equal(root.get("inviter").get("id"), inviterId);
    }

    public static Specification<GoalInvitation> hasInvitedId(Long invitedId) {
        return (root, query, cb) -> Objects.isNull(invitedId)
                ? cb.conjunction()
                : cb.equal(root.get("invited").get("id"), invitedId);
    }

    public static Specification<GoalInvitation> hasInviterNamePattern(String inviterNamePattern) {
        return (root, query, cb) -> Objects.isNull(inviterNamePattern)
                ? cb.conjunction()
                : cb.like(root.get("inviter").get("username"), "%" + inviterNamePattern + "%");
    }

    public static Specification<GoalInvitation> hasInvitedNamePattern(String invitedNamePattern) {
        return (root, query, cb) -> Objects.isNull(invitedNamePattern)
                ? cb.conjunction()
                : cb.like(root.get("invited").get("username"), "%" + invitedNamePattern + "%");
    }

    public static Specification<GoalInvitation> hasStatus(RequestStatus status) {
        return (root, query, cb) -> Objects.isNull(status)
                ? cb.conjunction()
                : cb.equal(root.get("status"), status);
    }
}
