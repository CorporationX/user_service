package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship_request.MentorshipResponseDto;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestFilterDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.filter.mentorship_request.MentorshipRequestFilter;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;

    @Transactional
    public MentorshipResponseDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        long requesterId = mentorshipRequestDto.getRequesterId();
        long receiverId = mentorshipRequestDto.getReceiverId();

        mentorshipRequestValidator.validateEqualsId(requesterId, receiverId);
        mentorshipRequestValidator.validateForEmptyRequester(requesterId);
        mentorshipRequestValidator.validateForEmptyReceiver(receiverId);

        Optional<MentorshipRequest> lastRequest = mentorshipRequestRepository.findLatestRequest(requesterId, receiverId);
        lastRequest.ifPresent(mentorshipRequestValidator::validateLastRequest);

        log.info("Create mentorship request from {} to {}", requesterId, receiverId);

        MentorshipRequest resultRequest = mentorshipRequestRepository.create(requesterId, receiverId, mentorshipRequestDto.getDescription());
        log.info("Mentorship request with ID: {} and with status: {} was created", resultRequest.getId(), resultRequest.getStatus());
        return mentorshipRequestMapper.mentorshipRequestToResponseDto(resultRequest);
    }

    @Transactional(readOnly = true)
    public List<MentorshipRequestDto> getRequests(MentorshipRequestFilterDto filters) {
        Supplier<Stream<MentorshipRequest>> requests = () -> mentorshipRequestRepository.findAll().stream();

        List<MentorshipRequest> filteredRequests = mentorshipRequestFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(requests, filters)).toList();

        return mentorshipRequestMapper.toRequestDtoList(filteredRequests);
    }

    @Transactional
    public MentorshipResponseDto acceptRequest(long id) {
        MentorshipRequest request = validateExistsRequestAndGet(id);

        User requester = request.getRequester();
        User receiver = request.getReceiver();

        mentorshipRequestValidator.validateExistMentorInRequesterList(requester, receiver);

        requester.getMentors().add(receiver);
        request.setStatus(RequestStatus.ACCEPTED);

        return mentorshipRequestMapper.mentorshipRequestToResponseDto(request);
    }

    @Transactional
    public MentorshipResponseDto rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest request = validateExistsRequestAndGet(id);
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());
        return mentorshipRequestMapper.mentorshipRequestToResponseDto(request);
    }

    private MentorshipRequest validateExistsRequestAndGet(long id) {
        Optional<MentorshipRequest> request = mentorshipRequestRepository.findById(id);
        if (request.isEmpty()) {
            String errMessage = "Mentorship request with ID " + id + " not found!";
            log.error(errMessage);
            throw new EntityNotFoundException(errMessage);
        }
        return request.get();
    }
}