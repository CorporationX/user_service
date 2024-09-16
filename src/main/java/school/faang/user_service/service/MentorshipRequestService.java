package school.faang.user_service.service;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import java.util.List;

@Component
public interface MentorshipRequestService {

    MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto);

    List<MentorshipRequestDto> getRequests(RequestFilterDto filters);

    MentorshipRequest getRequestById(long id);

    void acceptRequest(long id);

    RejectionDto rejectRequest(Long id, RejectionDto rejectionDto);
}
