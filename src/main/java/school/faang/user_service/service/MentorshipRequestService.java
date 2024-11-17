package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestService {
    private final UserService userService;
    private final MentorshipRequestRepository requestRepository;
    private final MentorshipRequestValidator requestValidator;
    private final MentorshipRequestMapper requestMapper;
    private final List<Filter<MentorshipRequest, RequestFilterDto>> filters;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto dto) {
        requestValidator.validateMentorshipRequest(dto);
        requestRepository.create(dto.getRequesterId(), dto.getReceiverId(), dto.getDescription());

        log.info("Mentorship request with id #{} from UserId #{} to UserId #{} created successfully.",
                dto.getId(), dto.getRequesterId(), dto.getReceiverId());
        return dto;
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filters) {
        Stream<MentorshipRequest> requests = requestRepository.findAll().stream();

        return this.filters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(requests, (requestsStream, filter) -> filter.apply(requestsStream, filters), (s1, s2) -> s1)
                .map(requestMapper::toDto)
                .toList();
    }

    @Transactional
    public MentorshipRequestDto acceptRequest(long id) {
        MentorshipRequest request = validateAndGetMentorshipRequest(id);
        User requester = request.getRequester();
        User receiver = request.getReceiver();

        requestValidator.validateRequesterHasReceiverAsMentor(requester, receiver);
        requester.getMentors().add(receiver);
        receiver.getMentees().add(requester);
        request.setStatus(RequestStatus.ACCEPTED);
        userService.saveUser(requester);
        userService.saveUser(receiver);

        log.info("Request with id #{} was accepted by UserId #{}.", id, receiver.getId());
        return requestMapper.toDto(requestRepository.save(request));
    }

    @Transactional
    public MentorshipRequestDto rejectRequest(long id, RejectionDto rejectionDto) {
        MentorshipRequest request = validateAndGetMentorshipRequest(id);
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejectionDto.getReason());

        log.info("Request with id #{} was rejected by UserId #{} with reason '{}'.",
                id, request.getReceiver().getId(), rejectionDto.getReason());
        return requestMapper.toDto(requestRepository.save(request));
    }

    private MentorshipRequest validateAndGetMentorshipRequest(long id) {
        requestValidator.validateMentorshipRequestExists(id);

        return requestRepository.findById(id).orElseThrow(() -> {
            log.warn("Request with id #{} not found.", id);
            return new EntityNotFoundException("Request with id #" + id + " not found.");
        });
    }
}

