package school.faang.user_service.service;

import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorshipRequest.RejectionDto;

import java.util.List;

public interface MentorshipRequestService {

    MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto);

    List<MentorshipRequestDto> getRequests(MentorshipRequestFilterDto requestFilter);

    MentorshipRequestDto acceptRequest(Long id);
    
    MentorshipRequestDto rejectRequest(long id, RejectionDto rejection);
}
