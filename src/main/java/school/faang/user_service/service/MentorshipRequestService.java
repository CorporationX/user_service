package school.faang.user_service.service;

import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;

import java.util.List;

public interface MentorshipRequestService {
    void requestMentorship(MentorshipRequestDto mentorshipRequestDto);

    List<MentorshipRequestDto> getRequests(RequestFilterDto requestFilterDto);

    void acceptRequest(Long id);

    void rejectRequest(Long id, RejectionDto rejection);
}
