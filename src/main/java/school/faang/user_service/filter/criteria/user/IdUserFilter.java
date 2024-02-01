package school.faang.user_service.filter.criteria.user;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;

@Component
public class IdUserFilter implements UserFilter<Long> {

    private final String filterFieldName = "id";

    @Override
    public boolean isApplicable(String targetFieldName) {
        return filterFieldName.equals(targetFieldName);
    }

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaBuilder cb, Long value) {
        return cb.equal(root.get(filterFieldName), value);
    }

}
