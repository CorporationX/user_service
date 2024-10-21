package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.mentorship.request.MentorshipRequestCreationDto;
import school.faang.user_service.dto.mentorship.request.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.request.MentorshipRequestRejectionDto;
import school.faang.user_service.dto.mentorship.request.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.publisher.MentorshipAcceptedEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.filter.RequestFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipRequestServiceImpl implements MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final UserContext userContext;
    private final MentorshipRequestMapper requestMapper;
    private final MentorshipAcceptedEventPublisher acceptedEventPublisher;
    private final List<RequestFilter> filters;

    @Setter
    @Value("${mentorship.request.time-constraint}")
    private int minTimeRequestConstraint;

    @Override
    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestCreationDto creationRequest) {
        validateRequesterId(creationRequest.requesterId());
        validateRequestCreation(creationRequest);
        MentorshipRequest request = requestMapper.toMentorshipRequest(creationRequest);
        request.setStatus(RequestStatus.PENDING);
        mentorshipRequestRepository.save(request);
        log.info("Created mentorship request requester id: %d, receiver id: %d"
                .formatted(creationRequest.requesterId(), creationRequest.receiverId()));
        return requestMapper.toMentorshipRequestDto(request);
    }

    @Override
    public List<MentorshipRequestDto> getRequests(RequestFilterDto filters) {
        List<MentorshipRequest> requests = mentorshipRequestRepository.findAll();
        List<MentorshipRequest> filteredRequests = getFilteredRequests(requests, filters);
        log.debug("Found %d mentorship requests".formatted(filteredRequests.size()));
        return requestMapper.toMentorshipRequestDtos(filteredRequests);
    }

    @Override
    public MentorshipRequestDto acceptRequest(Long requestId) {
        MentorshipRequest request = getPendingRequest(requestId);
        validateRequesterId(request.getReceiver().getId());
        request.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(request);
        acceptedEventPublisher.publish(requestMapper.toMentorshipAcceptedEventDto(request));
        log.info("Request with id %d accepted".formatted(requestId));
        return requestMapper.toMentorshipRequestDto(request);
    }

    @Override
    public MentorshipRequestDto rejectRequest(Long requestId, MentorshipRequestRejectionDto rejectionDto) {
        MentorshipRequest request = getPendingRequest(requestId);
        validateRequesterId(request.getReceiver().getId());
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejectionDto.reason());
        mentorshipRequestRepository.save(request);
        log.info("Request with id %d rejected".formatted(requestId));
        return requestMapper.toMentorshipRequestDto(request);
    }

    private List<MentorshipRequest> getFilteredRequests(List<MentorshipRequest> requests,
                                                        RequestFilterDto requestFilterDto) {
        Stream<MentorshipRequest> requestStream = requests.stream();
        for (RequestFilter filter : filters) {
            if (filter.isApplicable(requestFilterDto)) {
                requestStream = filter.apply(requestStream, requestFilterDto);
            }
        }
        return requestStream.toList();
    }

    private MentorshipRequest getPendingRequest(Long requestId) {
        MentorshipRequest request = mentorshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request with id %d not found".formatted(requestId)));
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new DataValidationException("Request with id %d is not pending".formatted(requestId));
        }
        return request;
    }

    private void validateUserId(Long userId) {
        if (userRepository.existsById(userId) == false) {
            throw new EntityNotFoundException("User with id %d not found".formatted(userId));
        }
    }

    private void validateRequesterId(Long requesterId) {
        Long currentUserId = userContext.getUserId();
        if (!Objects.equals(requesterId, currentUserId)) {
            throw new DataValidationException(
                    "Requester id %d doesn't match current user id: %d"
                            .formatted(requesterId, currentUserId));
        }
    }

    private void validateRequestCreation(MentorshipRequestCreationDto creationRequest) {
        if (Objects.equals(creationRequest.receiverId(), creationRequest.requesterId())) {
            throw new DataValidationException("User can't send mentorship request to himself, user id: %d"
                    .formatted(creationRequest.requesterId()));
        }
        validateUserId(creationRequest.requesterId());
        validateUserId(creationRequest.receiverId());
        if (mentorshipRequestRepository.existAcceptedRequest(
                creationRequest.requesterId(), creationRequest.receiverId())) {
            throw new DataValidationException(
                    "User with id %d already accepted mentorship request from user with id %d"
                            .formatted(creationRequest.receiverId(), creationRequest.requesterId()));
        }
        Optional<MentorshipRequest> latestRequest = mentorshipRequestRepository.findLatestRequest(
                creationRequest.requesterId(),
                creationRequest.receiverId());
        if (latestRequest.isPresent()) {
            LocalDateTime requestDate = latestRequest.get().getCreatedAt();
            if (requestDate.isAfter(LocalDateTime.now().minusMonths(minTimeRequestConstraint))) {
                throw new DataValidationException(
                        "Can't send request often than one in %d months, requester id: %d, receiver id: %d"
                                .formatted(
                                        minTimeRequestConstraint,
                                        creationRequest.requesterId(),
                                        creationRequest.receiverId()));
            }
        }
    }
}
