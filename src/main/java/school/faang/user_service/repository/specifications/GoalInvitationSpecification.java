package school.faang.user_service.repository.specifications;

import org.springframework.data.jpa.domain.Specification;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;


public class GoalInvitationSpecification {

    public static Specification<GoalInvitation> getByStatus(RequestStatus status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<GoalInvitation> getInvitedId(Long invitedId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("invited").get("id"), invitedId);
    }

    public static Specification<GoalInvitation> getInviterId(Long inviterId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("inviter").get("id"), inviterId);
    }

    public static Specification<GoalInvitation> invitedNamePattern(String invitedNamePattern) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("invited").get("username"), String.format("%%%s%%", invitedNamePattern));
    }

    public static Specification<GoalInvitation> inviterNamePattern(String inviterNamePattern) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("inviter").get("username"), String.format("%%%s%%", inviterNamePattern));
    }
}
