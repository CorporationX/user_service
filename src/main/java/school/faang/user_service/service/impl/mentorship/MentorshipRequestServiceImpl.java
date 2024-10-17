package school.faang.user_service.service.impl.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.model.dto.MentorshipRequestDto;
import school.faang.user_service.model.dto.Rejection;
import school.faang.user_service.model.dto.RequestFilter;
import school.faang.user_service.model.entity.MentorshipRequest;
import school.faang.user_service.model.event.MentorshipAcceptedEvent;
import school.faang.user_service.publisher.MentorshipAcceptedEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.util.predicate.NotApplicable;
import school.faang.user_service.util.predicate.PredicateTrue;
import school.faang.user_service.validator.MentorshipRequestValidator;
import school.faang.user_service.validator.RequestFilterPredicate;
import school.faang.user_service.validator.validatorResult.NotValidated;
import school.faang.user_service.validator.validatorResult.Validated;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static school.faang.user_service.model.entity.RequestStatus.ACCEPTED;
import static school.faang.user_service.model.entity.RequestStatus.REJECTED;
@Service
@RequiredArgsConstructor
public class MentorshipRequestServiceImpl implements MentorshipRequestService {
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipRequestRepository repository;
    private final RequestFilterPredicate requestFilterPredicate;
    private final MentorshipAcceptedEventPublisher mentorshipPublisher;

    public static final String MENTOR_IS_ALREADY_ACCEPTED = "mentor request is already accepter";

    @Override
    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        var response = mentorshipRequestValidator.validate(mentorshipRequestDto);
        if (!(response instanceof Validated)) {
            System.out.println(((NotValidated) response).getMessage());
        } else {
            repository.create(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId(), mentorshipRequestDto.getDescription());
        }

    }

    @Override
    public List<MentorshipRequest> getRequests(RequestFilter filter) {
        return repository.findAll().stream().filter(mentReq -> {
            return requestFilterPredicate.getRequestsFilterList().stream()
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

    @Override
    public void acceptRequest(long id) throws Exception {
        Optional<MentorshipRequest> requestOptional = repository.findById(id);
        if (requestOptional.isPresent() && requestOptional.get().getStatus() != ACCEPTED) {
            var request = requestOptional.get();
            request.setStatus(ACCEPTED);
            repository.save(request);
            mentorshipPublisher.publish(new MentorshipAcceptedEvent(request.getRequester().getId(),
                    request.getReceiver().getId(), request.getId()));
        } else if (requestOptional.isPresent() && requestOptional.get().getStatus() == ACCEPTED) {
            throw new Exception(MENTOR_IS_ALREADY_ACCEPTED);
        }
    }

    @Override
    public void rejectRequest(long id, Rejection rejection) {
        Optional<MentorshipRequest> requestOptional = repository.findById(id);

        if (requestOptional.isPresent()) {
            var request = requestOptional.get();
            request.setStatus(REJECTED);
            request.setRejectionReason(rejection.getReason());
            repository.save(request);
        }

    }

}
