package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mapper;
    private final List<RequestFilter> requestFilters;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto requestDto) {
        this.validateRequest(requestDto);

        MentorshipRequest mentorshipRequest = mapper.toEntity(requestDto);
        User requester = userRepository.findById(requestDto.getUserRequesterId())
                .orElseThrow(() -> new RuntimeException("User requester not found"));
        User receiver = userRepository.findById(requestDto.getUserReceiverId())
                .orElseThrow(() -> new RuntimeException("User receiver not found"));
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);

        return mapper.toDto(requestRepository.save(mentorshipRequest));
    }

    private void validateRequest(MentorshipRequestDto requestDto) {
        if (requestDto.getDescription().isBlank()) {
            throw new RuntimeException("Description is blank!");
        }

        if (Objects.equals(requestDto.getUserRequesterId(), requestDto.getUserReceiverId())) {
            throw new RuntimeException("User requester and user receiver are identical!");
        }

        if (!userRepository.existsById(requestDto.getUserRequesterId()))
            throw new RuntimeException("Requester are not exist in the database!");

        if (!userRepository.existsById(requestDto.getUserReceiverId())) {
            throw new RuntimeException("Receiver are not exist in the database!");
        }

        Optional<MentorshipRequest> latestRequest = requestRepository.findLatestRequest(requestDto.getUserRequesterId(),
                requestDto.getUserReceiverId());
        if (latestRequest.isPresent()) {
            MentorshipRequest mentorshipRequest = latestRequest.get();
            Period period = Period.between(mentorshipRequest.getCreatedAt().toLocalDate(),
                    requestDto.getCreatedAt().toLocalDate());
            if (period.getMonths() < 3)
                throw new RuntimeException("Latest request was less than 3 Months");
        }
    }

    public List<RequestFilterDto> getRequests(RequestFilterDto dto) {
        Stream<MentorshipRequest> requests = requestRepository.findAll().stream();

        requestFilters
                .stream()
                .filter(filter -> filter.isApplicable(dto))
                .forEach(filter -> filter.apply(requests, dto));

        return mapper.toDto(requests.toList());
    }

    public AcceptationDto acceptRequest(AcceptationDto acceptationDto) {
        if (acceptationDto.getRequestId() == null) {
            throw new RuntimeException("Request id is null!");
        }
        MentorshipRequest request = requestRepository.findById(acceptationDto.getRequestId())
                .orElseThrow(() -> new RuntimeException("Mentorship request does not exist!"));
        request.setStatus(RequestStatus.ACCEPTED);

        return mapper.toAcceptDto(requestRepository.save(request));
    }

    public RejectionDto rejectRequest(RejectionDto rejectionDto) {
        if (rejectionDto.getRequestId() == null) {
            throw new RuntimeException("Request id is null!");
        }
        MentorshipRequest request = requestRepository.findById(rejectionDto.getRequestId())
                .orElseThrow(() -> new RuntimeException("Mentorship request does not exist!"));
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason("I don't like you!");

        return mapper.toRejectDto(requestRepository.save(request));
    }
}
