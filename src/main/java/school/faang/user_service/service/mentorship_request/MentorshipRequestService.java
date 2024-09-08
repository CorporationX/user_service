package school.faang.user_service.service.mentorship_request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentorship_request.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.util.List;
import java.util.stream.Stream;

import static school.faang.user_service.service.mentorship_request.error_messages.MentorshipRequestErrorMessages.REQUEST_NOT_FOUND;

@Slf4j
@Component
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final List<RequestFilter> requestFilters;
    private final MentorshipRequestValidator validator;

    @Transactional
    public MentorshipRequest requestMentorship(long requesterId, long receiverId, String description) {
        log.info("Start validation of params");
        validator.validateRequest(requesterId, receiverId, description);
        log.info("Validation of params successful");
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.create(requesterId, receiverId, description);
        log.info("Mentorship request from user with id {} to user with id {} created", requesterId, receiverId);
        return mentorshipRequest;
    }

    @Transactional(readOnly = true)
    public List<MentorshipRequest> getRequests(RequestFilterDto filters) {
        Stream<MentorshipRequest> mentorshipRequests = mentorshipRequestRepository.findAll().stream();
        return requestFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(mentorshipRequests, (stream, filter) -> filter
                        .apply(stream, filters), (s1, s2) -> s1)
                .peek(request -> log.info("Mentorship request find: {}", request.getId()))
                .toList();
    }

    @Transactional
    public void acceptRequest(long id) {
        MentorshipRequest mentorshipRequest = findMentorshipRequestById(id);
        log.info("Request with id {} was found", id);
        User requester = mentorshipRequest.getRequester();
        User receiver = mentorshipRequest.getReceiver();
        validator.checkExistAcceptedRequest(requester.getId(), receiver.getId());
        log.info("Accept request from user with id {} to user with id {} not found", requester.getId(), receiver.getId());
        requester.getMentors().add(receiver);
        log.info("User with id {} added to the list of mentors by user with id {}", receiver.getId(), requester.getId());
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        log.info("Request with id {} accepted", id);
    }

    @Transactional
    public void rejectRequest(long id, String reason) {
        log.info("Find mentorship request with id {}", id);
        MentorshipRequest mentorshipRequest = findMentorshipRequestById(id);
        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        log.info("Request with id {} rejected", id);
        mentorshipRequest.setRejectionReason(reason);
        log.info("Rejection reason of mentorship request with id {} changed to {}", id, RequestStatus.REJECTED);
    }

    private MentorshipRequest findMentorshipRequestById(long id) {
        return mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Request with id {} not found", id);
                    return new IllegalArgumentException(
                            String.format(REQUEST_NOT_FOUND, id));
                });
    }
}
