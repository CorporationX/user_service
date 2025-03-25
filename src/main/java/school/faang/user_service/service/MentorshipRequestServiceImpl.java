package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.MentorshipRejectionDto;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.MentorshipAlreadyExistsException;
import school.faang.user_service.exception.MentorshipRequestAlreadyRejectException;
import school.faang.user_service.exception.MentorshipRequestFrequencyException;
import school.faang.user_service.exception.MentorshipRequestNotFoundException;
import school.faang.user_service.exception.SelfMentorshipRequestException;
import school.faang.user_service.filter.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MentorshipRequestServiceImpl implements MentorshipRequestService {

    @Value("${user_service.mentorship.allowedRequestFrequency}")
    private int allowedRequestFrequency;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final MentorshipService mentorshipService;
    private final UserService userService;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;

    @Override
    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        long requesterId = userService.getUniqueIdByUsername(mentorshipRequestDto.requesterUsername());
        long receiverId = userService.getUniqueIdByUsername(mentorshipRequestDto.receiverUsername());

        if (requesterId == receiverId) {
            throw new SelfMentorshipRequestException("User cannot request mentorship from themselves.");
        }
        Optional<MentorshipRequest> lastRequest = mentorshipRequestRepository
                .findLatestRequest(requesterId, receiverId);
        if (lastRequest.isPresent()) {
            LocalDateTime allowedRequestDate = lastRequest.get().getCreatedAt().plusMonths(allowedRequestFrequency);
            if (allowedRequestDate.isAfter(LocalDateTime.now())) {
                throw new MentorshipRequestFrequencyException("Next mentorship request allowed after: %s"
                        .formatted(allowedRequestDate.toString()));
            }
        }

        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(mentorshipRequestDto);
        mentorshipRequest.setRequester(userService.getReferenceById(requesterId));
        mentorshipRequest.setReceiver(userService.getReferenceById(receiverId));
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        MentorshipRequest savedRequests = mentorshipRequestRepository.save(mentorshipRequest);
        return mentorshipRequestMapper.toDto(savedRequests);
    }

    @Override
    @Transactional
    public List<MentorshipRequestDto> getRequests(MentorshipRequestFilterDto filter) {
        Stream<MentorshipRequest> filteredMentorshipRequests = mentorshipRequestRepository.findAll().stream();

        for (MentorshipRequestFilter mentorshipRequestFilter : mentorshipRequestFilters) {
            if (mentorshipRequestFilter.isApplicable(filter)) {
                filteredMentorshipRequests = mentorshipRequestFilter.apply(filteredMentorshipRequests, filter);
            }
        }
        return filteredMentorshipRequests
                .map(mentorshipRequestMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public MentorshipRequestDto acceptRequest(long id) {
        MentorshipRequest mentorshipRequest = getMentorshipRequestIfExist(id);
        long mentorId = mentorshipRequest.getReceiver().getId();
        long menteeId = mentorshipRequest.getRequester().getId();
        if (mentorshipService.existsByMentorIdAndMenteeId(mentorId, menteeId)) {
            throw new MentorshipAlreadyExistsException(
                    "Mentorship connection already exists between mentor %d and mentee %d"
                            .formatted(mentorId, menteeId));
        }

        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        mentorshipService.createMentorship(mentorId, menteeId);
        MentorshipRequest savedRequest = mentorshipRequestRepository.save(mentorshipRequest);

        return mentorshipRequestMapper.toDto(savedRequest);
    }

    @Override
    @Transactional
    public MentorshipRequestDto rejectRequest(long id, MentorshipRejectionDto rejection) {
        MentorshipRequest mentorshipRequest = getMentorshipRequestIfExist(id);

        if (mentorshipRequest.getStatus().equals(RequestStatus.REJECTED)) {
            throw new MentorshipRequestAlreadyRejectException("Mentorship request %d already rejected.".formatted(id));
        }

        mentorshipRequest.setRejectionReason(rejection.rejectionReason());
        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        MentorshipRequest savedRequest = mentorshipRequestRepository.save(mentorshipRequest);

        return mentorshipRequestMapper.toDto(savedRequest);
    }

    private MentorshipRequest getMentorshipRequestIfExist(long requestId) {
        return mentorshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new MentorshipRequestNotFoundException(
                        "Mentorship request %d not found.".formatted(requestId)));
    }
}
