package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.dto.MentorshipRequestDto;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.util.predicate.NotApplicable;
import school.faang.user_service.util.predicate.PredicateFalse;
import school.faang.user_service.util.predicate.PredicateResult;
import school.faang.user_service.validator.validatorResult.NotValidated;
import school.faang.user_service.validator.validatorResult.Validated;
import school.faang.user_service.validator.validatorResult.ValidationResult;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MentorshipRequestValidatorImpl implements MentorshipRequestValidator {
    private final MentorshipRequestRepository repository;
    private final MentorshipFilterPredicate predicates;

    @Override
    public ValidationResult validate(MentorshipRequestDto mentorshipRequestDto) {
        Optional<PredicateResult> failedPredicate = predicates.getMentorshipRequestPredicates()
                .stream()
                .map(predicate -> predicate.apply(repository, mentorshipRequestDto))
                .filter(result -> !(result instanceof NotApplicable))
                .filter(result -> {
                    return (result instanceof PredicateFalse);
                })
                .findFirst();

        if (failedPredicate.isPresent()) {
            return new NotValidated(((PredicateFalse) failedPredicate.get()).message());
        } else {
            return new Validated<>(null);
        }

    }
}
