package school.faang.user_service.service;

import static school.faang.user_service.constants.ErrorMessages.*;
import static school.faang.user_service.constants.InfoMessages.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentor.MentorshipRequestDto;
import school.faang.user_service.dto.mentor.RequestFilterDto;
import school.faang.user_service.dto.mentor.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.filter.mentor.RequestFilter;
import school.faang.user_service.mapper.RequestFilterMapper;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private static final int MIN_DESCRIPTION_LENGTH = 10;
    private static final int REQUEST_COOLDOWN_MONTHS = 3;

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final UserService userService;
    private final RequestFilterMapper requestFilterMapper;
    private final List<RequestFilter> filters;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        validateDto(mentorshipRequestDto, ERROR_NULL_MENTORSHIP_REQUEST_DTO);
        if (mentorshipRequestDto.getDescription().length() < MIN_DESCRIPTION_LENGTH) {
            String errorMessage = getShortDescriptionError(MIN_DESCRIPTION_LENGTH);
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        List<Long> missingUser = Stream.of(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId())
                .filter(id -> !mentorshipRequestRepository.existsById(id))
                .toList();
        if (!missingUser.isEmpty()) {
            String missingIds = missingUser.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            String errorMessage = getUserNotFoundError(missingIds);
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        if (mentorshipRequestDto.getRequesterId().equals(mentorshipRequestDto.getReceiverId())) {
            log.error(ERROR_SELF_REQUEST);
            throw new IllegalArgumentException(ERROR_SELF_REQUEST);
        }

        LocalDateTime threeMouthAgo = LocalDateTime.now().minusMonths(REQUEST_COOLDOWN_MONTHS);
        Optional<MentorshipRequest> recentRequest = mentorshipRequestRepository
                .findLatestRequest(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId());
        if (recentRequest.isPresent()) {
            if (recentRequest.get().getCreatedAt().isAfter(threeMouthAgo)) {
                String errorMessage = getFrequentRequestError(REQUEST_COOLDOWN_MONTHS);
                log.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }
        }
        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(mentorshipRequestDto);
        mentorshipRequest.setRequester(userService.findById(mentorshipRequestDto.getRequesterId()));
        mentorshipRequest.setReceiver(userService.findById(mentorshipRequestDto.getReceiverId()));
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        mentorshipRequestRepository.save(mentorshipRequest);
    }

    public List<RequestFilterDto> getRequests(RequestFilterDto filterRequestDto) {
        validateDto(filterRequestDto, ERROR_NULL_REQUEST_DTO);
        Stream<MentorshipRequest> requestStream = StreamSupport.stream(mentorshipRequestRepository.findAll()
                .spliterator(), false);

        for (RequestFilter filter : filters) {
            if (filter.isApplicable(filterRequestDto)) {
                requestStream = filter.apply(requestStream, filterRequestDto);
            }
        }
        return requestFilterMapper.toListDto(requestStream.toList());
    }

    public void acceptRequest(long id) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(getAbsentRequestError(id)));

        User receiver = mentorshipRequest.getReceiver();
        User requester = mentorshipRequest.getRequester();

        if (receiver.equals(requester)) {
            throw new IllegalArgumentException(ERROR_SELF_REQUEST);
        }

        if (receiver.getMentors().contains(requester)) {
            throw new IllegalArgumentException(ERROR_ALREADY_MENTOR);
        }
        receiver.getMentors().add(requester);
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequest.setReceiver(receiver);
        mentorshipRequestRepository.save(mentorshipRequest);
    }

    public void rejectRequest(long id, RejectionDto rejection) {
        validateDto(rejection, ERROR_NULL_REJECTION_DTO);

        if (rejection.getRejectionReason() == null || rejection.getRejectionReason().isBlank()) {
            throw new IllegalArgumentException(ERROR_EMPTY_REJECTION);
        }
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(getAbsentRequestError(id)));
        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejection.getRejectionReason());
        log.info(INFO_REJECTION_REASON, id, rejection.getRejectionReason());
        mentorshipRequestRepository.save(mentorshipRequest);
    }

    private <T> void validateDto(T dto, String errorMessage) {
        Objects.requireNonNull(dto, errorMessage);
    }
}
