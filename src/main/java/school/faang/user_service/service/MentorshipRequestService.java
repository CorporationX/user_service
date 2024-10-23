package school.faang.user_service.service;

import school.faang.user_service.model.dto.MentorshipRequestDto;
import school.faang.user_service.model.dto.Rejection;
import school.faang.user_service.model.dto.RequestFilter;
import school.faang.user_service.model.entity.MentorshipRequest;

import java.util.List;

public interface MentorshipRequestService {

    MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto);

    List<MentorshipRequest> getRequests(RequestFilter filter);

    void acceptRequest(long id) throws Exception;

    void rejectRequest(long id, Rejection rejection);
}
