package school.faang.user_service.service.mentorship;

import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;

import java.util.List;

public interface MentorshipRequestService {
    MentorshipRequestDto requestMentorship(Long requesterId, Long receiverId, MentorshipRequestDto dto);

    List<MentorshipRequestDto> getRequests(RequestFilterDto requestFilterDto);

    MentorshipRequestDto acceptRequest(Long id);

    MentorshipRequestDto rejectRequest(Long id, RejectionDto rejection);
}
