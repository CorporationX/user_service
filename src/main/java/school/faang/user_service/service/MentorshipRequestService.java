package school.faang.user_service.service;

import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.Rejection;
import school.faang.user_service.dto.RequestFilter;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;

public interface MentorshipRequestService {
    void requestMentorship(MentorshipRequestDto mentorshipRequestDto);
    List<MentorshipRequest> getRequests(RequestFilter filter);
    void acceptRequest(long id) throws Exception;
    void rejectRequest(long id, Rejection rejection);
}
