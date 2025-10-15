package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.mentorship_request.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipRequestServiceImpl implements MentorshipRequestService {
    private static final int MIN_MONTHS_BETWEEN = 3;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final UserContext userContext;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;


    public MentorshipRequestDto create(CreateMentorshipRequestDto requestDto) {
        validateMentorshipRequest(requestDto);

        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.create(
                userContext.getUserId(), requestDto.mentorId(), requestDto.description());
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        mentorshipRequest.setCreatedAt(LocalDateTime.now());

        return mentorshipRequestMapper.toMentorshipRequestDto(mentorshipRequestRepository.save(mentorshipRequest));
    }

    public MentorshipRequestDto toMentorshipRequestDto(long requestId) {
        MentorshipRequest request = mentorshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new DataValidationException("Request not found"));

        return mentorshipRequestMapper.toMentorshipRequestDto(request);
    }

    public List<MentorshipRequestDto> getByFilters(MentorshipRequestFilterDto filter) {
        Stream<MentorshipRequest> allMentorshipRequests = mentorshipRequestRepository.findAll().stream();

        for (MentorshipRequestFilter mentorshipRequestFilter : mentorshipRequestFilters) {
            if (mentorshipRequestFilter.isApplicable(filter)) {
                allMentorshipRequests = mentorshipRequestFilter.apply(allMentorshipRequests, filter);
            }
        }

        return allMentorshipRequests
                .map(mentorshipRequestMapper::toMentorshipRequestDto)
                .toList();
    }

    public void accept(long requestId) {
        validateMentor(requestId);
        MentorshipRequest request = mentorshipRequestRepository.findById(requestId).orElseThrow();

        request.setStatus(RequestStatus.ACCEPTED);

        mentorshipRequestRepository.save(request);
        log.info("Mentor {} has accepted your {}", request.getReceiver().getUsername(), request.getId());
    }

    public void reject(long requestId, RejectionDto rejectionDto) {
        validateMentor(requestId);
        MentorshipRequest request = mentorshipRequestRepository.findById(requestId).orElseThrow();

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejectionDto.reason());

        mentorshipRequestRepository.save(request);
        log.info("Mentor {} has rejected your {}", request.getReceiver().getUsername(), request.getId());
    }

    private void validateMentorshipRequest(CreateMentorshipRequestDto requestDto) {
        long currentUserId = userContext.getUserId();

        if (currentUserId == requestDto.mentorId()) {
            throw new ForbiddenException("Can't send request to yourself");
        }

        Optional<MentorshipRequest> latestRequestOptional =
                mentorshipRequestRepository.findLatestRequest(currentUserId, requestDto.mentorId());

        if (latestRequestOptional.isPresent()) {
            MentorshipRequest latestRequest = latestRequestOptional.get();

            LocalDateTime now = LocalDateTime.now();
            long monthsBetween = ChronoUnit.MONTHS.between(latestRequest.getCreatedAt(), now);

            if (monthsBetween < MIN_MONTHS_BETWEEN) {
                throw new DataValidationException("Сan send request only every three months");
            }

            if (latestRequest.getStatus() != RequestStatus.ACCEPTED) {
                throw new ForbiddenException("He already your mentor");
            }
        }
    }

    private void validateMentor(long requestId) {
        long currentUserId = userContext.getUserId();

        MentorshipRequest request = mentorshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new DataValidationException("Request not found"));

        if (!Objects.equals(request.getReceiver().getId(), currentUserId)) {
            throw new ForbiddenException("Only receiver of the request can accept it.");
        }
    }
}
