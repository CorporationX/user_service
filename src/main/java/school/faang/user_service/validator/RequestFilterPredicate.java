package school.faang.user_service.validator;

import school.faang.user_service.dto.RequestFilter;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.util.predicate.PredicateResult;

import java.util.List;
import java.util.function.BiFunction;


public interface RequestFilterPredicate {

    List<BiFunction<MentorshipRequest, RequestFilter, PredicateResult>> getRequestsFilterList();

}
