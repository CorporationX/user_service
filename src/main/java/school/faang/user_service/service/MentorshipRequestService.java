package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private static final int MIN_DESCRIPTION_LENGTH = 10;
    private static final int REQUEST_COOLDOWN_MONTHS = 3;

    private static final String ERROR_NULL_DTO = "MentorshipRequestDto can't be null.";
    private static final String ERROR_SHORT_DESCRIPTION = String.format("Description should be at least %d characters long.\n",
            MIN_DESCRIPTION_LENGTH);
    private static final String ERROR_SELF_REQUEST = "You cannot request mentorship from yourself.";
    private static final String ERROR_USER_NOT_FOUND = "User with the given ID(s): %s was not found.";
    private static final String ERROR_TOO_FREQUENT_REQUESTS = String.format("You can only request mentorship once every %d months.\n",
            REQUEST_COOLDOWN_MONTHS);

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final UserService userService;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        validateMentorshipRequestDto(mentorshipRequestDto);
        if (mentorshipRequestDto.getDescription().length() < MIN_DESCRIPTION_LENGTH) {
            log.error(ERROR_SHORT_DESCRIPTION);
            throw new IllegalArgumentException(ERROR_SHORT_DESCRIPTION);
        }

        List<Long> missingUser = Stream.of(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId())
                .filter(id -> !mentorshipRequestRepository.existsById(id))
                .toList();
        if (!missingUser.isEmpty()) {
            String missingIds = missingUser.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            log.info(String.format(ERROR_USER_NOT_FOUND, missingIds));
            throw new IllegalArgumentException(String.format(ERROR_USER_NOT_FOUND, missingIds));
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
                log.error(ERROR_TOO_FREQUENT_REQUESTS);
                throw new IllegalArgumentException(ERROR_TOO_FREQUENT_REQUESTS);
            }
        }

        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(mentorshipRequestDto);
        mentorshipRequest.setRequester(userService.findById(mentorshipRequestDto.getRequesterId()));
        mentorshipRequest.setReceiver(userService.findById(mentorshipRequestDto.getReceiverId()));
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        mentorshipRequestRepository.save(mentorshipRequest);
    }

    private void validateMentorshipRequestDto(MentorshipRequestDto mentorshipRequestDto) {
        Objects.requireNonNull(mentorshipRequestDto, ERROR_NULL_DTO);
    }
}
