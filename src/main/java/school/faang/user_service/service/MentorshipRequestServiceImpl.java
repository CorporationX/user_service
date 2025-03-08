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
import school.faang.user_service.filter.RequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
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
    private final UserService userService;
    private final List<RequestFilter> requestFilters;

    @Value("${app.mentorship-request.min-request-interval-in-months}")
    private int minRequestIntervalInMonths;

    @Override
    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        Long requesterId = mentorshipRequestDto.getRequesterId();
        Long receiverId = mentorshipRequestDto.getReceiverId();

        ensureUserExistsById(requesterId);
        ensureUserExistsById(receiverId);

        mentorshipRequestRepository
                .findLatestRequest(requesterId, receiverId)
                .ifPresent((mentorshipRequest -> {
                            if (mentorshipRequest
                                    .getCreatedAt()
                                    .plusMonths(minRequestIntervalInMonths)
                                    .isAfter(LocalDateTime.now())) {
                                log.warn("Прошлый запрос был менее {} месяцев назад.", minRequestIntervalInMonths);
                                throw new DataValidationException(String.format(
                                        "Прошлый запрос был менее %d месяцев назад.", minRequestIntervalInMonths));
                            }
                        })
                );
        ensureRequesterIsNotReceiver(requesterId, receiverId);
        mentorshipRequestRepository.create(requesterId, receiverId, mentorshipRequestDto.getDescription());
    }

    @Override
    public List<MentorshipRequestDto> getRequests(RequestFilterDto requestFilterDto) {
        Stream<MentorshipRequest> filteredMentorshipRequests = StreamSupport
                .stream(mentorshipRequestRepository.findAll().spliterator(), false);

        for (RequestFilter requestFilter : requestFilters) {
            if (requestFilter.isApplicable(requestFilterDto)) {
                requestFilter.apply(filteredMentorshipRequests, requestFilterDto);
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

                            throwIfUserAlreadyInList(requester.getMentors(),
                                    receiver, "{} уже есть в списке менторов {}",
                                    requester, "%s уже есть в списке менторов %s");

                            throwIfUserAlreadyInList(receiver.getMentees(),
                                    requester, "{} уже в списке учеников {}",
                                    receiver, "%s уже в списке учеников %s");

                            requester.getMentors().add(receiver);
                            receiver.getMentees().add(requester);

                            mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
                        },
                        () -> throwIfRequestNotFound(id)
                );
    }

    @Override
    public void rejectRequest(Long id, RejectionDto rejection) {
        mentorshipRequestRepository
                .findById(id)
                .ifPresentOrElse((mentorshipRequest -> {
                            mentorshipRequest.setStatus(RequestStatus.REJECTED);
                            mentorshipRequest.setRejectionReason(rejection.reason());
                        }),
                        () -> throwIfRequestNotFound(id));
    }

    private static void ensureRequesterIsNotReceiver(Long requesterId, Long receiverId) {
        if (requesterId.equals(receiverId)) {
            log.warn("Id {} того кто запрашивает менторство и Id {} того у кого запрашивают равны.",
                    requesterId, receiverId);
            throw new DataValidationException(String.format(
                    "Id %s того кто запрашивает менторство и Id %s того у кого запрашивают равны.",
                    requesterId, receiverId));
        }
    }

    private static void throwIfRequestNotFound(Long id) {
        log.warn("Запроса с id {} нету в базе данных", id);
        throw new DataValidationException(String.format("Запроса с id %d нету в базе данных", id));
    }

    private static void throwIfUserAlreadyInList(List<User> requester, User receiver, String errorMessage, User requester1, String format) {
        if (requester.contains(receiver)) {
            log.error(errorMessage, receiver, requester1);
            throw new DataValidationException(
                    String.format(format, receiver, requester1));
        }
    }

    private void ensureUserExistsById(Long id) {
        if (!userService.existsById(id)) {
            log.error("Нету пользователя с id {}", id);
            throw new IllegalArgumentException("Нету пользователя с id " + id);
        }
    }
}
