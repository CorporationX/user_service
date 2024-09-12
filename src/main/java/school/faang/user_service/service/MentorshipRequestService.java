package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilter;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.util.predicate.NotApplicable;
import school.faang.user_service.util.predicate.PredicateTrue;
import school.faang.user_service.validator.MentorshipRequestValidator;
import school.faang.user_service.validator.Predicates;
import school.faang.user_service.validator.validatorResult.NotValidated;
import school.faang.user_service.validator.validatorResult.Validated;

import java.util.List;
import java.util.function.Predicate;

import static school.faang.user_service.entity.RequestStatus.ACCEPTED;
import static school.faang.user_service.entity.RequestStatus.REJECTED;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipRequestRepository repository;
    private final Predicates predicates;

    public static final String MENTOR_IS_ALREADY_ACCEPTED = "mentor request is already accepter";

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        val response = mentorshipRequestValidator.validate(mentorshipRequestDto);
        if (!(response instanceof Validated)) {
            System.out.println(((NotValidated) response).getMessage());
        } else {
            repository.create(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId(), mentorshipRequestDto.getDescription());
        }

    }

    public List<MentorshipRequest> getRequests(RequestFilter filter) {
        return repository.findAll().stream().filter(mentReq -> {
            return predicates.requestsFilterList.stream()
                    .map(predicate -> predicate.apply(mentReq, filter))
                    .filter(result -> !(result instanceof NotApplicable)) // Исключаем NotApplicable
                    .map(result -> {
                        if (result instanceof PredicateTrue) {
                            return (Predicate<MentorshipRequest>) req -> true; // Predicate that always returns true
                        } else {
                            return (Predicate<MentorshipRequest>) req -> false; // Predicate that always returns false
                        }
                    })
                    .reduce(Predicate::and) // Combine all predicates using AND
                    .orElse(req -> false).test(mentReq);

        }).toList();
    }

    public void acceptRequest(long id) throws Exception {
        MentorshipRequest request = repository.getMentorshipRequestById(id);
        if (request.getStatus() != ACCEPTED) {
            repository.updateMentorshipRequestStatusByRequesterId(id, ACCEPTED);
        } else if (request.getStatus() == ACCEPTED) {
            throw new Exception(MENTOR_IS_ALREADY_ACCEPTED);
        }
    }

    public void rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest request = repository.getMentorshipRequestById(id);
        repository.updateMentorshipRequestStatusWithReasonByRequesterId(id, REJECTED, rejection.getReason());

    }

}
