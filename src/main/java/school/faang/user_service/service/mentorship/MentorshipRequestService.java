package school.faang.user_service.service.mentorship;

import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;

import java.util.List;

public interface MentorshipRequestService {

    MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto);

    List<MentorshipRequestDto> getRequests(RequestFilterDto filter);

    MentorshipRequestDto acceptRequest(long id);

    MentorshipRequestDto rejectRequest(long id, RejectionDto rejectionDto);
}
