package school.faang.user_service.service;

import school.faang.user_service.model.dto.MentorshipRequestDto;
import school.faang.user_service.model.dto.RejectionDto;
import school.faang.user_service.model.filter_dto.MentorshipRequestFilterDto;

import java.util.List;

public interface MentorshipRequestService {
    MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto);

    List<MentorshipRequestDto> getRequests(MentorshipRequestFilterDto requestFilter);

    MentorshipRequestDto acceptRequest(Long id);

    MentorshipRequestDto rejectRequest(long id, RejectionDto rejection);
}
