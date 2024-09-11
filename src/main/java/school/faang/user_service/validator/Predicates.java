package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RequestFilter;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.util.predicate.NotApplicable;
import school.faang.user_service.util.predicate.PredicateFalse;
import school.faang.user_service.util.predicate.PredicateResult;
import school.faang.user_service.util.predicate.PredicateTrue;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@Component
//@NoArgsConstructor
@RequiredArgsConstructor
public class Predicates {
    public final String REQUEST_AND_RECEIVER_HAS_SAME_ID = "user can not request mentorschihp to himself";
    public final String USERS_NOT_EXIST_IN_DATABASE = "user are not exists in database";
    public final String REQUEST_TIME_EXEEDED = "The request was updated within the last 3 months.";
    public final String REQUEST_WAS_NOT_FOUND = "request was not found";

    public final BiFunction<MentorshipRequestRepository, MentorshipRequestDto, PredicateResult> userExistsPredicate = (repository, dto) -> {
        if (dto.getRequesterId()==null){
            return new NotApplicable();
        }
        if (repository.existAcceptedRequest(dto.getRequesterId(), dto.getReceiverId())) {
            return new PredicateTrue();
        } else {
            return new PredicateFalse(USERS_NOT_EXIST_IN_DATABASE);
        }
    };

    public final BiFunction<MentorshipRequestRepository, MentorshipRequestDto, PredicateResult> sameUserPredicate = (repository, dto) -> {
        if (dto.getRequesterId() == null || dto.getReceiverId() == null){
            return new NotApplicable();
        }
        if (Objects.equals(dto.getRequesterId(), dto.getReceiverId())) {
            return new PredicateFalse( REQUEST_AND_RECEIVER_HAS_SAME_ID);
        } else {
            return new PredicateTrue();
        }
    };

    public final BiFunction<MentorshipRequestRepository, MentorshipRequestDto, PredicateResult> requestTimeExceededPredicate = (repository, dto) -> {
        if (dto.getCreatedAt() == null || dto.getUpdatedAt() == null){
            return new NotApplicable();
        }
        Optional<MentorshipRequest> request = repository.findLatestRequest(dto.getRequesterId(), dto.getReceiverId());
        if (request.isEmpty()) {
            return new PredicateFalse(REQUEST_WAS_NOT_FOUND);
        } else if (request.get().getUpdatedAt().isBefore(LocalDateTime.now().minus(3, ChronoUnit.MONTHS))) {
            return new PredicateTrue();
        } else {
            return new PredicateFalse( REQUEST_TIME_EXEEDED);
        }
    };

    public final List<BiFunction<MentorshipRequestRepository, MentorshipRequestDto, PredicateResult>>
            mentorshipRequestPredicates = List.of(userExistsPredicate,requestTimeExceededPredicate,sameUserPredicate);

    public final Predicate<MentorshipRequest> isDescriptionEmptyPredicate = (request) -> {
        return request.getDescription().isEmpty();
    };

    public final BiPredicate<MentorshipRequest, RequestFilter> areAuthorsMatch = (request, filter) -> {
        return request.getRequester().getId().equals(filter.getRequesterId());
    };

    public final BiPredicate<MentorshipRequest, RequestFilter> isRecieverMatch = (request, filter) -> {
        return request.getReceiver().getId().equals(filter.getReceiverId());
    };

    public final BiPredicate<MentorshipRequest, RequestFilter> isStatusMatch = (request, filter) -> {
        return request.getStatus().equals(filter.getStatus());
    };

}
