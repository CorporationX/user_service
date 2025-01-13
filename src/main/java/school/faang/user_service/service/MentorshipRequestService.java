package school.faang.user_service.service;

import school.faang.user_service.dto.MentorshipRejectionDto;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipRequestFilterDto;

import java.util.List;

public interface MentorshipRequestService {
    MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto);
    MentorshipRequestDto acceptRequest(Long id);
    MentorshipRequestDto rejectRequest(MentorshipRejectionDto rejection);
    List<MentorshipRequestDto> getRequests(MentorshipRequestFilterDto filters);
}