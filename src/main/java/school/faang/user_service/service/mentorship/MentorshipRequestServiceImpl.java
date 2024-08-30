package school.faang.user_service.service.mentorship;

import static school.faang.user_service.exception.ExceptionMessages.DELETION_ERROR_MESSAGE;

import jakarta.persistence.PersistenceException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.AopInvocationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.exception.mentorship.MentorshipIsAlreadyAgreedException;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.messaging.publisher.mentorship.request.MentorshipAcceptedEventPublisher;
import school.faang.user_service.messaging.publisher.mentorship.request.MentorshipOfferedEventPublisher;
import school.faang.user_service.messaging.publisher.mentorship.request.MentorshipRequestedEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.mentorship.MentorshipValidator;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipRequestServiceImpl implements MentorshipRequestService {

    private static final String MESSAGE_ABOUT_DELETE_MENTORSHIP_REQUESTS
        = "Все запросы на менторство/менти были удалены.";

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mapper;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;
    private final List<MentorshipValidator> mentorshipValidators;
    private final MentorshipAcceptedEventPublisher mentorshipAcceptedPublisher;
    private final MentorshipRequestedEventPublisher mentorshipRequestedPublisher;
    private final MentorshipOfferedEventPublisher mentorshipOfferedEventPublisher;

    @Override
    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        mentorshipValidators.forEach(validator -> validator.validate(mentorshipRequestDto));
        mentorshipRequestDto.setRequestStatus(RequestStatus.PENDING);
        MentorshipRequest savedRequest;
        try {
            var entityToBeSaved = mapper.toEntity(mentorshipRequestDto);
            savedRequest = mentorshipRequestRepository.save(entityToBeSaved);
        } catch (Exception e) {
            log.error(ExceptionMessages.FAILED_PERSISTENCE, e);
            throw new PersistenceException(ExceptionMessages.FAILED_PERSISTENCE, e);
        }
        mentorshipRequestedPublisher.publish(mapper.toMentorshipRequestedEvent(savedRequest));
        mentorshipOfferedEventPublisher.publish(mapper.toMentorshipOfferedEvent(savedRequest));

        return mapper.toDto(savedRequest);
    }

    @Override
    public List<MentorshipRequestDto> getRequests(RequestFilterDto filters) {
        try {
            var requests = getFilteredRequests(filters);
            return requests.stream().map(mapper::toDto).toList();
        } catch (Exception e) {
            log.error(ExceptionMessages.FAILED_RETRIEVAL, e);
            throw new PersistenceException(ExceptionMessages.FAILED_RETRIEVAL, e);
        }
    }

    @Override
    @Transactional
    public MentorshipRequestDto acceptRequest(long id) {
        var request = findMentorshipRequest(id);
        var requester = request.getRequester();
        var receiver = request.getReceiver();
        if (receiver.getMentees().contains(requester)) {
            throw new MentorshipIsAlreadyAgreedException(ExceptionMessages.MENTORSHIP_ALREADY_ONGOING);
        } else {
            receiver.getMentees().add(requester);
            requester.getMentors().add(receiver);
            request.setStatus(RequestStatus.ACCEPTED);
            mentorshipRequestRepository.save(request);
            mentorshipAcceptedPublisher.publish(mapper.toMentorshipAcceptedEvent(request));
        }
        return mapper.toDto(request);
    }

    @Override
    public MentorshipRequestDto rejectRequest(long id, RejectionDto rejectionDto) {
        var request = findMentorshipRequest(id);
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejectionDto.reason());
        return mapper.toDto(mentorshipRequestRepository.save(request));
    }

    private List<MentorshipRequest> getFilteredRequests(RequestFilterDto filters) {
        var requestsStream = StreamSupport.stream(mentorshipRequestRepository.findAll().spliterator(), false);
        if (filters == null) {
            return requestsStream.toList();
        } else {
            return mentorshipRequestFilters.stream()
                    .filter(filter -> filter.isApplicable(filters))
                    .flatMap(filter -> filter.apply(requestsStream, filters))
                    .toList();
        }
    }

    private MentorshipRequest findMentorshipRequest(long id) {
        return mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(ExceptionMessages.MENTORSHIP_REQUEST_NOT_FOUND));
    }

    /**
     * Метод для удаления отправленных или полученных заявок на менторство/менти пользователя.
     * @param userId  id пользователя, чей аккаунт деактивируется.
     */
    public void deleteMentorshipRequests(long userId) {
        try {
            mentorshipRequestRepository.deleteAllMentorshipRequestById(userId);
            log.info(MESSAGE_ABOUT_DELETE_MENTORSHIP_REQUESTS);
        } catch (AopInvocationException e) {
            log.error(DELETION_ERROR_MESSAGE, e);
        }
    }
}
