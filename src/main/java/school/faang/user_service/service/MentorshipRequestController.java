package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.validator.MentorshipRequestValidator;

@Service
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;
    private final MentorshipRequestValidator mentorshipRequestValidator;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }
     void getRequests(RequestFilterDto filter){
         mentorshipRequestService.getRequests(filter);
    }

    void acceptRequest(long id) throws Exception {
        mentorshipRequestService.acceptRequest( id);
    }

    void rejectRequest(long id, RejectionDto rejection){
        mentorshipRequestService.rejectRequest(id,rejection);
    }
}
