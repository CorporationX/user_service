package school.faang.user_service.service.mentorship_request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.annotation.SendMentorshipRequestReceived;
import school.faang.user_service.dto.mentorship_request.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.BadRequestException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.util.List;
import java.util.stream.Stream;

import static school.faang.user_service.entity.RequestStatus.ACCEPTED;
import static school.faang.user_service.entity.RequestStatus.REJECTED;
import static school.faang.user_service.service.mentorship_request.error_messages.MentorshipRequestErrorMessages.REQUEST_NOT_FOUND;

@Slf4j
@Component
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final List<RequestFilter> requestFilters;
    private final MentorshipRequestParametersChecker checker;
    private final UserRepository userRepository;

    @SendMentorshipRequestReceived
    @Transactional
    public MentorshipRequest requestMentorship(long requesterId, long receiverId, String description) {
        checker.checkRequestParams(requesterId, receiverId, description);
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
    public MentorshipRequest acceptRequest(long id) {
        MentorshipRequest mentorshipRequest = findMentorshipRequestById(id);
        User requester = mentorshipRequest.getRequester();
        User receiver = mentorshipRequest.getReceiver();
        checker.checkExistAcceptedRequest(requester.getId(), receiver.getId());
        requester.getMentors().add(receiver);
        userRepository.save(requester);
        mentorshipRequest.setStatus(ACCEPTED);
        log.info("Request with id {} accepted", id);
        return mentorshipRequestRepository.save(mentorshipRequest);
    }

    @Transactional
    public MentorshipRequest rejectRequest(long id, String reason) {
        MentorshipRequest mentorshipRequest = findMentorshipRequestById(id);
        mentorshipRequest.setStatus(REJECTED);
        mentorshipRequest.setRejectionReason(reason);
        log.info("Request with id {} rejected", id);
        return mentorshipRequestRepository.save(mentorshipRequest);
    }

    private MentorshipRequest findMentorshipRequestById(long id) {
        return mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Request with id {} not found", id);
                    return new BadRequestException(REQUEST_NOT_FOUND, id);
                });
    }
}
