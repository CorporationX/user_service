package school.faang.user_service.service.mentorship;

import school.faang.user_service.dto.mentorship.request.MentorshipRequestCreationDto;
import school.faang.user_service.dto.mentorship.request.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.request.MentorshipRequestRejectionDto;
import school.faang.user_service.dto.mentorship.request.RequestFilterDto;

import java.util.List;

public interface MentorshipRequestService {
    MentorshipRequestDto requestMentorship(MentorshipRequestCreationDto creationRequest);

    List<MentorshipRequestDto> getRequests(RequestFilterDto filters);

    MentorshipRequestDto acceptRequest(Long requestId);

    MentorshipRequestDto rejectRequest(Long requestId, MentorshipRequestRejectionDto rejectionDto);
}
