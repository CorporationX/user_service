package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.PredicateResult;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.validatorResult.NotValidated;
import school.faang.user_service.validator.validatorResult.Validated;
import school.faang.user_service.validator.validatorResult.ValidationResult;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class MentorshipRequestValidator implements Validator<MentorshipRequestDto,MentorshipRequestRepository, PredicateResult> {


    private final MentorshipRequestRepository repository;

    @Override
    public ValidationResult validate(MentorshipRequestDto mentorshipRequestDto, List<BiFunction<MentorshipRequestRepository, MentorshipRequestDto, PredicateResult>> predicates) {
        Optional<PredicateResult> failedPredicate = predicates
                .stream()
                .map(predicate -> predicate.apply(repository, mentorshipRequestDto))
                .filter(result -> !result.getResult())
                .findFirst();

        if (failedPredicate.isPresent()) {
            return new NotValidated(failedPredicate.get().getMessage());
        } else {
            return new Validated<>(null);
        }

    }
}
