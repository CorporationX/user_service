package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestEventDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.publisher.EventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.filters.RequestFilter;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestMapper requestMapper;
    private final MentorshipRequestRepository requestRepository;
    private final MentorshipRequestValidator requestValidator;
    private final UserRepository userRepository;
    private final List<RequestFilter> requestFilters;
    private final EventPublisher publisher;

    public MentorshipRequest requestMentorship(MentorshipRequest requestEntity) {
        long requesterId = requestEntity.getRequester().getId();
        long receiverId = requestEntity.getReceiver().getId();

        if (!requestValidator.validateLastRequestData(requesterId, receiverId)) {
            throw new DataValidationException("Too early for next mentorship request");
        } else {
            User requester = userRepository.findById(requesterId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            User receiver = userRepository.findById(receiverId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            requestEntity.setRequester(requester);
            requestEntity.setReceiver(receiver);

            List<MentorshipRequest> requests = requester.getSentMentorshipRequests();
            if (requests == null) {
                requests = new ArrayList<>();
            }
            requestEntity.setStatus(RequestStatus.PENDING);
            requests.add(requestEntity);
            requester.setSentMentorshipRequests(requests);

            userRepository.save(requester);
            MentorshipRequest savedRequest = requestRepository.save(requestEntity);

            MentorshipRequestEventDto eventM = new MentorshipRequestEventDto();
            eventM.setRequesterId(requesterId);
            eventM.setMentorId(receiverId);
            eventM.setRequestId(savedRequest.getId());
            publisher.publishEvent("mentorship_request", eventM);

            return savedRequest;
        }
    }

    public List<MentorshipRequest> getRequests(RequestFilterDto filtersRequested) {
        Stream<MentorshipRequest> mentorshipRequests = requestRepository.findAll().stream();
        return requestFilters.stream()
                .filter(filter -> filter.isApplicable(filtersRequested))
                .reduce(mentorshipRequests,
                        (stream, filter) -> filter.apply(stream, filtersRequested),
                        (stream1, stream2) -> stream1)
                .toList();
    }

    public MentorshipRequestDto acceptRequest(long id) {
        MentorshipRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        User requester = request.getRequester();
        User receiver = request.getReceiver();
        requestValidator.validateNotMentorYet(requester, receiver);

        requester.getMentors().add(receiver);
        userRepository.save(requester);

        receiver.getMentees().add(requester);
        userRepository.save(receiver);

        request.setStatus(RequestStatus.ACCEPTED);
        request.setCreatedAt(LocalDateTime.now());

        return requestMapper.toDto(requestRepository.save(request));
    }

    public MentorshipRequest rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getRejectionReason());
        return requestRepository.save(request);
    }
}
