package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.AcceptationDto;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.filters.RequestFilter;

import java.time.Period;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.apache.commons.lang3.ObjectUtils.anyNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class MentorshipRequestService {

    public final String DESCRIPTION_IS_BLANK = "Description is blank";
    public final String IDENTICAL_USERS = "User requester and user receiver are identical";
    public final String USER_NOT_EXIST = "User is not exist in the database";
    public final String LATEST_REQUEST_LESS_THEN = "Latest request was less than 3 Months";

    private final MentorshipRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mapper;
    private final List<RequestFilter> requestFilters;

    public MentorshipRequest requestMentorship(MentorshipRequest request) {
        this.validateRequest(request);

        User requester = userRepository.getById(request.getRequester().getId());
        User receiver = userRepository.getById(request.getReceiver().getId());
        request.setRequester(requester);
        request.setReceiver(receiver);

        return requestRepository.save(request);
    }

    private void validateRequest(MentorshipRequest request) {
        if (request.getDescription().isBlank()) {
            log.error(DESCRIPTION_IS_BLANK);
            throw new RuntimeException(DESCRIPTION_IS_BLANK);
        }

        if (Objects.equals(request.getRequester().getId(), request.getReceiver().getId())) {
            log.error("Requester id = {}, Receiver id = {}: {}", request.getRequester().getId(),
                    request.getReceiver().getId(), IDENTICAL_USERS);
            throw new RuntimeException(IDENTICAL_USERS);
        }

        if (!userRepository.existsById(request.getRequester().getId())) {
            log.error("Requester id = {}: {}", request.getRequester().getId(), USER_NOT_EXIST);
            throw new RuntimeException(USER_NOT_EXIST);
        }

        if (!userRepository.existsById(request.getReceiver().getId())) {
            log.error("Receiver id = {}: {}", request.getReceiver().getId(), USER_NOT_EXIST);
            throw new RuntimeException(USER_NOT_EXIST);
        }

        Optional<MentorshipRequest> latestRequest = requestRepository.findLatestRequest(request.getRequester().getId(),
                request.getReceiver().getId());
        if (latestRequest.isPresent()) {
            MentorshipRequest mentorshipRequest = latestRequest.get();
            Period period = Period.between(mentorshipRequest.getCreatedAt().toLocalDate(),
                    request.getCreatedAt().toLocalDate());
            if (period.getMonths() < 3) {
                log.error("Latest request was {}: {}", mentorshipRequest.getCreatedAt().toLocalDate(), LATEST_REQUEST_LESS_THEN);
                throw new RuntimeException(LATEST_REQUEST_LESS_THEN);
            }
        }
    }

    public List<MentorshipRequest> getRequests(RequestFilterDto dto) {
        Stream<MentorshipRequest> requests = requestRepository.findAll().stream();

        return requestFilters
                .stream()
                .filter(f -> f.isApplicable(dto))
                .reduce(requests,
                        (stream, filter) -> filter.apply(stream, dto),
                        (s1, s2) -> s1)
                .toList();
    }

    public AcceptationDto acceptRequest(AcceptationDto acceptationDto) {
//        if (acceptationDto.getRequestId() == null) {
//            throw new RuntimeException("Request id is null!");
//        }
        MentorshipRequest request = requestRepository.findById(acceptationDto.getRequestId())
                .orElseThrow(() -> new RuntimeException("Mentorship request does not exist!"));
        request.setStatus(RequestStatus.ACCEPTED);

        return mapper.toAcceptDto(requestRepository.save(request));
    }

    public RejectionDto rejectRequest(RejectionDto rejectionDto) {

        MentorshipRequest request = requestRepository.findById(rejectionDto.getRequestId())
                .orElseThrow(() -> new RuntimeException("Mentorship request does not exist!"));
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason("I don't like you!");
        MentorshipRequest savedMentorshipRequest = requestRepository.save(request);
        return mapper.toRejectDto(savedMentorshipRequest);
    }
}
