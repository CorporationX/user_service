package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentorship.MentorshipOfferedEvent;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.MentorshipRequestNotFoundException;
import school.faang.user_service.exception.RequestAlreadyAcceptedException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.publisher.MentorshipOfferedEventPublisher;
import school.faang.user_service.service.mentorship.filter.MentorshipRequestFilter;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipRequestMapper requestMapper;
    private final List<MentorshipRequestFilter> filters;
    private final MentorshipOfferedEventPublisher publisher;

    @Transactional
    public void requestMentorship(MentorshipRequestDto requestDto) {
        long requesterId = requestDto.getRequester();
        long receiverId = requestDto.getReceiver();

        dataValidate(requesterId, receiverId, requestDto);
        mentorshipRequestRepository.create(requesterId, receiverId, requestDto.getDescription());
        publisher.sendEvent(new MentorshipOfferedEvent(requesterId, receiverId));
        log.info("Mentorship request from requesterId={} to receiverId={} has been saved in DB successfully", requesterId, receiverId);
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filterDto) {
        List<MentorshipRequest> allRequests = new ArrayList<>();
        mentorshipRequestRepository.findAll()
                .forEach(request -> allRequests.add(request));
        Stream<MentorshipRequest> requestStream = allRequests.stream();

        List<MentorshipRequestFilter> applicableFilters = filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .toList();
        for (MentorshipRequestFilter filter : applicableFilters) {
            requestStream = filter.apply(requestStream, filterDto);
        }

        log.info("Requests have been taken from DB with filters successfully");
        return requestStream.map(requestMapper::toDto).toList();
    }

    public void acceptRequest(long id) {
        MentorshipRequest mentorshipRequest = getRequest(id);

        User receiver = mentorshipRequest.getReceiver();
        User requester = mentorshipRequest.getRequester();

        checkIsRequestAlreadyAccepted(mentorshipRequest, RequestStatus.ACCEPTED);

        if (requester.getMentors() == null) {
            requester.setMentors(List.of(receiver));
            if (receiver.getMentees() == null) {
                receiver.setMentees(List.of(requester));
            } else {
                receiver.getMentees().add(requester);
            }
            mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        } else if (!requester.getMentors().contains(receiver)) {
            requester.getMentors().add(receiver);
            receiver.getMentees().add(requester);
            mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        } else {
            throw new RequestAlreadyAcceptedException("This user has already been mentor for requester");
        }
        log.info("The request accepted successfully, requestId={}", id);
    }

    public void rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest mentorshipRequest = getRequest(id);

        User requester = mentorshipRequest.getRequester();
        User receiver = mentorshipRequest.getReceiver();

        checkIsRequestAlreadyAccepted(mentorshipRequest, RequestStatus.REJECTED);

        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejection.getReason());
        requester.getMentors().remove(receiver);
        receiver.getMentees().remove(requester);
        log.info("The request rejected successfully, requestId={}", id);
    }

    private void checkIsRequestAlreadyAccepted(MentorshipRequest request, RequestStatus status ) {
        if (request.getStatus() == status) {
            throw new RequestAlreadyAcceptedException("This request has already been accepted");
        }
    }

    private MentorshipRequest getRequest(long id) {
        return mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new MentorshipRequestNotFoundException("This mentorship request does not exist"));
    }

    private void dataValidate(long requesterId, long receiverId, MentorshipRequestDto requestDto) {
        checkIfUsersExistsAndNotSame(requesterId, receiverId);

        if (mentorshipRequestRepository.findLatestRequest(requesterId, receiverId).isPresent()) {
            MentorshipRequest latestRequest = mentorshipRequestRepository.findLatestRequest(requesterId, receiverId).get();
            if (latestRequest.getUpdatedAt().plusMonths(3).isAfter(LocalDateTime.now())) {
                throw new DataValidationException("You cannot send a mentorship request to this user " +
                        "because it must have been at least 3 months since the last request");
            }
        }
    }

    private void checkIfUsersExistsAndNotSame(long requesterId, long receiverId) {
        if (!mentorshipRepository.existsById(requesterId)) {
            throw new UserNotFoundException("This requester does not exist");
        }
        if (!mentorshipRepository.existsById(receiverId)) {
            throw new UserNotFoundException("This receiver does not exist");
        }
        if (requesterId == receiverId) {
            throw new DataValidationException("The user cannot send himself a mentorship request");
        }
    }
}