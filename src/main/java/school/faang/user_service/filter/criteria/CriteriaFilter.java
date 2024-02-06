package school.faang.user_service.filter.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public interface CriteriaFilter<T, V> {

    boolean isApplicable(String targetFieldName);

    Predicate toPredicate(Root<T> root, CriteriaBuilder cb, V value);

}
