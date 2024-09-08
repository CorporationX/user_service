package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.PredicateResult;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.validatorResult.NotValidated;
import school.faang.user_service.validator.validatorResult.Validated;
import school.faang.user_service.validator.validatorResult.ValidationResult;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class MentorshipRequestValidator implements Validator<MentorshipRequestDto> {

    public final String REQUEST_AND_RECEIVER_HAS_SAME_ID = "user can not request mentorschihp to himself";
    public final String USERS_NOT_EXIST_IN_DATABASE = "user are not exists in database";
    public final String REQUEST_TIME_EXEEDED = "The request was updated within the last 3 months.";
    public final String REQUEST_WAS_NOT_FOUND = "request was not found";

    private final MentorshipRequestRepository repository;


    final BiFunction<MentorshipRequestRepository, MentorshipRequestDto, PredicateResult> userExistsPredicate = (repository, dto) -> {
        if (repository.existAcceptedRequest(dto.getRequesterId(), dto.getReceiverId())) {
            return new PredicateResult(true, null);
        } else {
            return new PredicateResult(false, USERS_NOT_EXIST_IN_DATABASE);
        }
    };

    final BiFunction<MentorshipRequestRepository, MentorshipRequestDto, PredicateResult> sameUserPredicate = (repository, dto) -> {
        if (Objects.equals(dto.getRequesterId(), dto.getReceiverId())) {
            return new PredicateResult(false, REQUEST_AND_RECEIVER_HAS_SAME_ID);
        } else {
            return new PredicateResult(true, null);
        }
    };

    final BiFunction<MentorshipRequestRepository, MentorshipRequestDto, PredicateResult> requestTimeExceededPredicate = (repository, dto) -> {
        Optional<MentorshipRequest> request = repository.findLatestRequest(dto.getRequesterId(), dto.getReceiverId());
        if (request.isEmpty()) {
            return new PredicateResult(false, REQUEST_WAS_NOT_FOUND);
        } else if (request.get().getUpdatedAt().isBefore(LocalDateTime.now().minus(3, ChronoUnit.MONTHS))) {
            return new PredicateResult(true, null);
        } else {
            return new PredicateResult(false, REQUEST_TIME_EXEEDED);
        }
    };

    @Override
    public ValidationResult validate(MentorshipRequestDto mentorshipRequestDto) {
        Optional<PredicateResult> failedPredicate = List.of(userExistsPredicate, sameUserPredicate, requestTimeExceededPredicate)
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
