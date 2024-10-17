package school.faang.user_service.service.impl.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.model.dto.MentorshipRequestDto;
import school.faang.user_service.model.dto.Rejection;
import school.faang.user_service.model.dto.RequestFilter;
import school.faang.user_service.model.entity.MentorshipRequest;
import school.faang.user_service.model.event.MentorshipStartEvent;
import school.faang.user_service.publisher.MentorshipStartEventPublisher;
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

    public static final String MENTOR_IS_ALREADY_ACCEPTED = "mentor request is already accepter";

    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipRequestRepository repository;
    private final RequestFilterPredicate requestFilterPredicate;
    private final MentorshipStartEventPublisher mentorshipStartEventPublisher;

    @Override
    @Transactional
    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        var response = mentorshipRequestValidator.validate(mentorshipRequestDto);
        if (!(response instanceof Validated)) {
            System.out.println(((NotValidated) response).getMessage());
        } else {
            repository.create(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId(), mentorshipRequestDto.getDescription());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorshipRequest> getRequests(RequestFilter filter) {
        return repository.findAll().stream().filter(mentReq -> requestFilterPredicate.getRequestsFilterList().stream()
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
                .orElse(req -> false).test(mentReq)).toList();
    }

    @Override
    @Transactional
    public void acceptRequest(long id) throws Exception {
        Optional<MentorshipRequest> requestOptional = repository.findById(id);
        if (requestOptional.isPresent() && requestOptional.get().getStatus() != ACCEPTED) {
            var request = requestOptional.get();
            request.setStatus(ACCEPTED);
            sendMentorshipStartEvent(request);
            repository.save(request);
        } else {
            if (requestOptional.isPresent()) {
                throw new Exception(MENTOR_IS_ALREADY_ACCEPTED);
            }
        }
    }

    @Override
    @Transactional
    public void rejectRequest(long id, Rejection rejection) {
        Optional<MentorshipRequest> requestOptional = repository.findById(id);
        if (requestOptional.isPresent()) {
            var request = requestOptional.get();
            request.setStatus(REJECTED);
            request.setRejectionReason(rejection.getReason());
            repository.save(request);
        }
    }

    private MentorshipStartEvent buildMentorshipStartEvent(MentorshipRequest request) {
        return MentorshipStartEvent.builder()
                .mentorId(request.getRequester().getId())
                .menteeId(request.getReceiver().getId())
                .build();
    }

    private void sendMentorshipStartEvent(MentorshipRequest request) {
        MentorshipStartEvent event = buildMentorshipStartEvent(request);
        mentorshipStartEventPublisher.publish(event);
    }
}
