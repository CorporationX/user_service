package school.faang.user_service.validator;

import school.faang.user_service.model.dto.MentorshipRequestDto;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.util.predicate.PredicateResult;

import java.util.List;
import java.util.function.BiFunction;

public interface MentorshipFilterPredicate {

    List<BiFunction<MentorshipRequestRepository, MentorshipRequestDto, PredicateResult>> getMentorshipRequestPredicates();

}
