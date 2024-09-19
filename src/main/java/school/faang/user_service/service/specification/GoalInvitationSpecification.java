package school.faang.user_service.service.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.Objects;

public class GoalInvitationSpecification {
    public static Specification<GoalInvitation> getAllFiltered(Long inviterId,
                                                               Long invitedId,
                                                               String inviterNamePattern,
                                                               String invitedNamePattern,
                                                               Integer requestStatus) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (Objects.nonNull(inviterId)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("inviter").get("id"), inviterId));
            }

            if (Objects.nonNull(invitedId)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("invited").get("id"), invitedId));
            }

            if (Objects.nonNull(inviterNamePattern)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("inviter").get("username"), "%" + inviterNamePattern + "%"));
            }

            if (Objects.nonNull(invitedNamePattern)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("invited").get("username"), "%" + invitedNamePattern + "%"));
            }

            if (Objects.nonNull(requestStatus)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), requestStatus));
            }

            return predicate;
        };
    }
}
