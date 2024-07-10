package school.faang.user_service.service.mentorship;

import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.exception.mentorship.MentorshipIsAlreadyAgreedException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.validator.mentorship.MentorshipValidator;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultMentorshipRequestService implements MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mapper;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;
    private final List<MentorshipValidator> mentorshipValidators;

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
        var request = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(ExceptionMessages.MENTORSHIP_REQUEST_NOT_FOUND));
        var requester = request.getRequester();
        var receiver = request.getReceiver();
        if (receiver.getMentees().contains(requester)) {
            throw new MentorshipIsAlreadyAgreedException(ExceptionMessages.MENTORSHIP_ALREADY_ONGOING);
        } else {
            receiver.getMentees().add(requester);
            requester.getMentors().add(receiver);
            request.setStatus(RequestStatus.ACCEPTED);
            mentorshipRequestRepository.save(request);
        }
        return mapper.toDto(request);
    }

    private List<MentorshipRequest> getFilteredRequests(RequestFilterDto filters) {
        var requestsStream = StreamSupport.stream(mentorshipRequestRepository.findAll().spliterator(), false);
        if (filters == null) {
            return requestsStream.toList();
        } else {
            return mentorshipRequestFilters.stream()
                    .filter(filter -> filter.isApplicable(filters))
                    .flatMap(filter -> applyFilter(requestsStream, filter, filters))
                    .toList();
        }
    }

    private Stream<MentorshipRequest> applyFilter(Stream<MentorshipRequest> requestsStream, MentorshipRequestFilter filter, RequestFilterDto filters) {
        return filter.apply(requestsStream, filters);
    }
}
