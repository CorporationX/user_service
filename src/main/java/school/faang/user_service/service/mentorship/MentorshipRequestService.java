package school.faang.user_service.service.mentorship;

import school.faang.user_service.dto.mentorship.MentorshipRejectionDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;

import java.util.List;

public interface MentorshipRequestService {
    MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto);
    MentorshipRequestDto acceptRequest(Long id);
    MentorshipRequestDto rejectRequest(MentorshipRejectionDto rejection);
    List<MentorshipRequestDto> getRequests(MentorshipRequestFilterDto filters);
}