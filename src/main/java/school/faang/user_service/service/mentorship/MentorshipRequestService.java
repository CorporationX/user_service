package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final List<MentorshipRequestFilter> mentorshipRequestFilterList;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        long requesterId = mentorshipRequestDto.getRequesterId();
        long receiverId = mentorshipRequestDto.getReceiverId();
        String description = mentorshipRequestDto.getDescription();
        LocalDateTime mentorshipCreationDate = mentorshipRequestDto.getCreatedAt();

        mentorshipRequestValidator.validateMentorshipRequestReceiverAndRequesterExistence(requesterId, receiverId);
        mentorshipRequestValidator.validateReflection(requesterId, receiverId);
        mentorshipRequestValidator.validateMentorshipRequestFrequency(requesterId, receiverId, mentorshipCreationDate);
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.create(requesterId, receiverId, description);
        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }

    public List<MentorshipRequestDto> getRequests(MentorshipRequestFilterDto filtersDto) {
        List<MentorshipRequest> allMatchedRequests = selectAllMentorshipRequestsAndFilter(filtersDto);
        return allMatchedRequests.stream().map(mentorshipRequestMapper::toDto).toList();
    }

    private List<MentorshipRequest> selectAllMentorshipRequestsAndFilter(MentorshipRequestFilterDto filtersDto) {
        List<MentorshipRequestFilter> mentorshipRequestApplicableFilters =
                mentorshipRequestFilterList.stream().filter(filter -> filter.isApplicable(filtersDto)).toList();
        List<MentorshipRequest> allMentorshipRequests =
                StreamSupport.stream(mentorshipRequestRepository.findAll().spliterator(), false).toList();
        return allMentorshipRequests.stream().filter(request -> mentorshipRequestApplicableFilters.stream()
                .allMatch(filter -> filter.filter(request, filtersDto))).toList();
    }

    public MentorshipRequestDto acceptRequest(long requestId) {
        MentorshipRequest mentorshipRequest = getMentorshipRequestByIdOrThrowException(requestId);
        mentorshipRequestValidator.validateRequestStatusIsPending(mentorshipRequest.getStatus());
        MentorshipRequest savedMentorshipRequest =
                setRequestStatusAndSaveToDataBase(mentorshipRequest, RequestStatus.ACCEPTED);
        return mentorshipRequestMapper.toDto(savedMentorshipRequest);
    }

    public MentorshipRequestDto rejectRequest(long requestId, RejectionDto rejectionDto) {
        MentorshipRequest mentorshipRequest = getMentorshipRequestByIdOrThrowException(requestId);
        mentorshipRequestValidator.validateRequestStatusIsPending(mentorshipRequest.getStatus());
        mentorshipRequest.setRejectionReason(rejectionDto.getRejectionReason());
        setRequestStatusAndSaveToDataBase(mentorshipRequest, RequestStatus.REJECTED);
        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }

    private MentorshipRequest setRequestStatusAndSaveToDataBase(MentorshipRequest mentorshipRequest,
                                                                RequestStatus requestStatus) {
        mentorshipRequest.setStatus(requestStatus);
        mentorshipRequest.setUpdatedAt(LocalDateTime.now());
        return mentorshipRequestRepository.save(mentorshipRequest);
    }

    private MentorshipRequest getMentorshipRequestByIdOrThrowException(long requestId) {
        return mentorshipRequestRepository.findById(requestId).orElseThrow(
                () -> new IllegalArgumentException("Could not find Mentorship Request in database by id: " + requestId)
        );
    }
}
