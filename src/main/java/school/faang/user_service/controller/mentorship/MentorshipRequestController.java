package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;
    private final MentorshipRequestMapper mentorshipRequestMapper;

    public MentorshipRequestDto toMentorshipRequestDto(MentorshipRequest mentorshipRequest) {
        validateMentorshipRequest(mentorshipRequest);

        return mentorshipRequestService.toMentorshipRequestDto(mentorshipRequest.getId());
    }

    public List<MentorshipRequest> getByFilters(MentorshipRequestFilterDto filter) {
        validateMentorshipRequestFilterDto(filter);

        return mentorshipRequestService.getByFilters(filter).stream()
                .map(mentorshipRequestMapper::toMentorshipRequest)
                .toList();
    }

    public void accept(long requestId) {
        mentorshipRequestService.accept(requestId);
    }

    public void reject(long requestId, RejectionDto rejectionDto) {
        validateRejectionDto(rejectionDto);

        mentorshipRequestService.reject(requestId, rejectionDto);
    }

    private void validateMentorshipRequest(MentorshipRequest mentorshipRequest) {
        if (mentorshipRequest == null || mentorshipRequest.getRequester() == null
                || mentorshipRequest.getReceiver() == null || mentorshipRequest.getDescription() == null
                || mentorshipRequest.getDescription().isBlank()) {
            throw new IllegalArgumentException("Incorrect mentoring request data");
        }
    }

    private void validateMentorshipRequestFilterDto(MentorshipRequestFilterDto filter) {
        if (filter.requesterId() == null && filter.receiverId() == null) {
            throw new IllegalArgumentException("Filter must contain requesterId or receiverId");
        }
    }

    private void validateRejectionDto(RejectionDto rejectionDto) {
        if (rejectionDto.reason() == null || rejectionDto.reason().isBlank()) {
            throw new IllegalArgumentException("Specify reason for the rejection");
        }
    }
}
