package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipRequestServiceImpl implements MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final UserRepository userRepository;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;

    @Value("${app.mentorship-request.min-request-interval-in-months}")
    private int minRequestIntervalInMonths;

    @Override
    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        Long requesterId = mentorshipRequestDto.getRequesterId();
        Long receiverId = mentorshipRequestDto.getReceiverId();

        ensureUserExistsById(requesterId);
        ensureUserExistsById(receiverId);

        checkingForDifferentIDs(requesterId, receiverId);

        mentorshipRequestRepository
                .findLatestRequest(requesterId, receiverId)
                .ifPresent((this::validateRequestInterval));
        mentorshipRequestRepository.create(requesterId, receiverId, mentorshipRequestDto.getDescription());
    }

    @Override
    public List<MentorshipRequestDto> getRequests(RequestFilterDto requestFilterDto) {
        Stream<MentorshipRequest> filteredMentorshipRequests =
                StreamSupport.stream(mentorshipRequestRepository.findAll().spliterator(), false);

        for (MentorshipRequestFilter mentorshipRequestFilter : mentorshipRequestFilters) {
            if (mentorshipRequestFilter.isApplicable(requestFilterDto)) {
                filteredMentorshipRequests = mentorshipRequestFilter
                        .apply(filteredMentorshipRequests, requestFilterDto);
            }
        }
        return filteredMentorshipRequests.map(mentorshipRequestMapper::toDto).toList();
    }

    @Override
    public void acceptRequest(Long id) {
        mentorshipRequestRepository
                .findById(id)
                .ifPresentOrElse(mentorshipRequest -> {
                            User requester = mentorshipRequest.getRequester();
                            User receiver = mentorshipRequest.getReceiver();

                            validateUserNotInList(requester.getMentors(), receiver);
                            validateUserNotInList(receiver.getMentees(), requester);

                            requester.getMentors().add(receiver);
                            receiver.getMentees().add(requester);

                            mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
                        },
                        () -> throwIfRequestNotFound(id)
                );
    }

    @Override
    public void rejectRequest(Long id, RejectionDto rejection) {
        mentorshipRequestRepository.findById(id)
                .ifPresentOrElse((mentorshipRequest -> {
                            mentorshipRequest.setStatus(RequestStatus.REJECTED);
                            mentorshipRequest.setRejectionReason(rejection.getReason());
                        }),
                        () -> throwIfRequestNotFound(id));
    }

    private void checkingForDifferentIDs(Long requesterId, Long receiverId) {
        if (requesterId.equals(receiverId)) {
            log.warn("ID {} The one who requests mentor and ID {} of the one whom they are requested equal.",
                    requesterId, receiverId);
            throw new DataValidationException(String.format(
                    "ID %s of the one who requests mentor and ID %s of the one who is requested equal.",
                    requesterId, receiverId));
        }
    }

    private void throwIfRequestNotFound(Long id) {
        log.warn("Request with ID {} is not in the database", id);
        throw new DataValidationException(String.format("Request with id %d is not in the database", id));
    }

    private void validateUserNotInList(
            List<User> users, User userFirst) {
        if (users.contains(userFirst)) {
            log.error("{} Already in the list {}", userFirst, users);
            throw new DataValidationException(
                    String.format("%s already has a %s list", userFirst, users));
        }
    }

    private void ensureUserExistsById(Long id) {
        if (!userRepository.existsById(id)) {
            log.error("There is no user with ID {}", id);
            throw new IllegalArgumentException(String.format("There is no user with ID %d", id));
        }
    }

    private void validateRequestInterval(MentorshipRequest mentorshipRequest) {
        if (mentorshipRequest
                .getCreatedAt()
                .plusMonths(minRequestIntervalInMonths)
                .isAfter(LocalDateTime.now())) {
            log.warn("The last request was less {} months ago.", minRequestIntervalInMonths);
            throw new DataValidationException(String.format(
                    "The last request was less than %d months ago.", minRequestIntervalInMonths));
        }
    }
}
