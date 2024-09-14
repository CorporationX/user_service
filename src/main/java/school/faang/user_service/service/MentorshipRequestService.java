package school.faang.user_service.service;

import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.Rejection;
import school.faang.user_service.dto.RequestFilter;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;

public interface MentorshipRequestService {
    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto);
    public List<MentorshipRequest> getRequests(RequestFilter filter);
    public void acceptRequest(long id) throws Exception;
    public void rejectRequest(long id, Rejection rejection);
}
