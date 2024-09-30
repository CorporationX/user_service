package school.faang.user_service.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.model.dto.MentorshipRequestDto;
import school.faang.user_service.model.dto.RequestFilter;
import school.faang.user_service.model.entity.MentorshipRequest;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.util.predicate.NotApplicable;
import school.faang.user_service.util.predicate.PredicateFalse;
import school.faang.user_service.util.predicate.PredicateResult;
import school.faang.user_service.util.predicate.PredicateTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
@Component
public class PredicatesImpl implements RequestFilterPredicate, MentorshipFilterPredicate {
    public static final String REQUEST_AND_RECEIVER_HAS_SAME_ID = "user can not request mentorschihp to himself";
    public static final String USERS_NOT_EXIST_IN_DATABASE = "user are not exists in database";
    public static final String REQUEST_TIME_EXEEDED = "The request was updated within the last 3 months.";
    public static final String REQUEST_WAS_NOT_FOUND = "request was not found";
    public static final String REQUESTER_ID_DONT_MATCH = "authors are not match";
    public static final String RECEIVER_ID_DONT_MATCH = "receiver are not match";
    public static final String STATUS_DONT_MATCH = "status are not match";

    public final BiFunction<MentorshipRequestRepository, MentorshipRequestDto, PredicateResult> userExistsPredicate = (repository, dto) -> {
        if (dto.getRequesterId() == null) {
            return new NotApplicable();
        }
        if (repository.existAcceptedRequest(dto.getRequesterId(), dto.getReceiverId())) {
            return new PredicateTrue();
        } else {
            return new PredicateFalse(USERS_NOT_EXIST_IN_DATABASE);
        }
    };

    public final BiFunction<MentorshipRequestRepository, MentorshipRequestDto, PredicateResult> sameUserPredicate = (repository, dto) -> {
        if (dto.getRequesterId() == null || dto.getReceiverId() == null) {
            return new NotApplicable();
        }
        if (Objects.equals(dto.getRequesterId(), dto.getReceiverId())) {
            return new PredicateFalse(REQUEST_AND_RECEIVER_HAS_SAME_ID);
        } else {
            return new PredicateTrue();
        }
    };

    public final BiFunction<MentorshipRequestRepository, MentorshipRequestDto, PredicateResult> requestTimeExceededPredicate = (repository, dto) -> {
        if (dto.getCreatedAt() == null || dto.getUpdatedAt() == null) {
            return new NotApplicable();
        }
        Optional<MentorshipRequest> request = repository.findLatestRequest(dto.getRequesterId(), dto.getReceiverId());
        if (request.isEmpty()) {
            return new PredicateFalse(REQUEST_WAS_NOT_FOUND);
        } else if (request.get().getUpdatedAt().isBefore(LocalDateTime.now().minusMonths(3))) {
            return new PredicateTrue();
        } else {
            return new PredicateFalse(REQUEST_TIME_EXEEDED);
        }
    };

    public final BiFunction<MentorshipRequest, RequestFilter, PredicateResult> isDescriptionEmptyPredicate = (request, filter) -> {
        if (filter.getDescription() == null || filter.getDescription().isEmpty()) {
            return new NotApplicable();
        } else {
            return new PredicateTrue();
        }
    };

    public final BiFunction<MentorshipRequest, RequestFilter, PredicateResult> areAuthorsMatch = (request, filter) -> {
        if (filter.getRequesterId() == null) {
            return new NotApplicable();
        }
        if (request.getRequester().getId().equals(filter.getRequesterId())) {
            return new PredicateTrue();
        } else {
            return new PredicateFalse(REQUESTER_ID_DONT_MATCH);
        }
    };

    public final BiFunction<MentorshipRequest, RequestFilter, PredicateResult> isRecieverMatch = (request, filter) -> {
        if (filter.getReceiverId() == null) {
            return new NotApplicable();
        }
        if (request.getReceiver().getId().equals(filter.getReceiverId())) {
            return new PredicateTrue();
        } else {
            return new PredicateFalse(RECEIVER_ID_DONT_MATCH);
        }
    };

    public final BiFunction<MentorshipRequest, RequestFilter, PredicateResult> isStatusMatch = (request, filter) -> {
        if (filter.getStatus() == null) {
            return new NotApplicable();
        }
        if (request.getStatus().equals(filter.getStatus())) {
            return new PredicateTrue();
        } else {
            return new PredicateFalse(STATUS_DONT_MATCH);
        }
    };


    @Override
    public List<BiFunction<MentorshipRequestRepository, MentorshipRequestDto, PredicateResult>> getMentorshipRequestPredicates() {
        return List.of(userExistsPredicate, requestTimeExceededPredicate, sameUserPredicate);
    }

    @Override
    public List<BiFunction<MentorshipRequest, RequestFilter, PredicateResult>> getRequestsFilterList() {
        return List.of(areAuthorsMatch, isRecieverMatch, isStatusMatch);
    }
}

