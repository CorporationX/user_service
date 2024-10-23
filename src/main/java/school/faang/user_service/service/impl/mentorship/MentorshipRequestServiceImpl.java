package school.faang.user_service.service.impl.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.model.dto.MentorshipRequestDto;
import school.faang.user_service.model.dto.Rejection;
import school.faang.user_service.model.dto.RequestFilter;
import school.faang.user_service.model.entity.MentorshipRequest;
import school.faang.user_service.model.event.MentorshipAcceptedEvent;
import school.faang.user_service.publisher.MentorshipAcceptedEventPublisher;
import school.faang.user_service.model.event.MentorshipRequestedEvent;
import school.faang.user_service.publisher.MentorshipRequestedEventPublisher;
import school.faang.user_service.model.event.MentorshipStartEvent;
import school.faang.user_service.publisher.MentorshipStartEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.util.predicate.NotApplicable;
import school.faang.user_service.util.predicate.PredicateTrue;
import school.faang.user_service.validator.RequestFilterPredicate;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static school.faang.user_service.model.entity.RequestStatus.ACCEPTED;
import static school.faang.user_service.model.entity.RequestStatus.REJECTED;

@Service
@RequiredArgsConstructor
public class MentorshipRequestServiceImpl implements MentorshipRequestService {
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipAcceptedEventPublisher mentorshipPublisher;
    private final RequestFilterPredicate requestFilterPredicate;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final MentorshipRequestedEventPublisher mentorshipRequestedEventPublisher;
    private final MentorshipStartEventPublisher mentorshipStartEventPublisher;

    @Override
    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestValidator.validateMentorshipRequest(mentorshipRequestDto);
        mentorshipRequestValidator.validateDateCreateRequest(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId());
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.create(mentorshipRequestDto.getRequesterId(),
                mentorshipRequestDto.getReceiverId(), mentorshipRequestDto.getDescription());

        var mentorshipRequestedEvent = MentorshipRequestedEvent.builder()
                .userId(mentorshipRequest.getRequester().getId())
                .receiverId(mentorshipRequest.getReceiver().getId())
                .requestedAt(mentorshipRequest.getCreatedAt())
                .build();

        mentorshipRequestedEventPublisher.publish(mentorshipRequestedEvent);
        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorshipRequest> getRequests(RequestFilter filter) {
        return mentorshipRequestRepository.findAll().stream().filter(mentReq -> {
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
    @Transactional
    public void acceptRequest(long id) {
        Optional<MentorshipRequest> requestOptional = mentorshipRequestRepository.findById(id);
        if (requestOptional.isPresent() && requestOptional.get().getStatus() != ACCEPTED) {
            var request = requestOptional.get();
            request.setStatus(ACCEPTED);
            mentorshipRequestRepository.save(request);
            mentorshipPublisher.publish(new MentorshipAcceptedEvent(request.getRequester().getId(),
                    request.getReceiver().getId(), request.getId()));
            sendMentorshipStartEvent(request);
            mentorshipRequestRepository.save(request);
        } else if (requestOptional.isPresent() && requestOptional.get().getStatus() == ACCEPTED) {
            throw new DataValidationException("Mentor request is already accepter");
        }
    }

    @Override
    @Transactional
    public void rejectRequest(long id, Rejection rejection) {
        Optional<MentorshipRequest> requestOptional = mentorshipRequestRepository.findById(id);
        if (requestOptional.isPresent()) {
            var request = requestOptional.get();
            request.setStatus(REJECTED);
            request.setRejectionReason(rejection.getReason());
            mentorshipRequestRepository.save(request);
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
