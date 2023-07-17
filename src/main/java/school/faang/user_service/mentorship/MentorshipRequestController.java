package school.faang.user_service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mentorship.dto.MentorshipRequestDto;
import school.faang.user_service.mentorship.dto.RejectionDto;
import school.faang.user_service.mentorship.dto.RequestFilterDto;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship")
public class MentorshipRequestController {

    @Autowired
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/mentorship/request")
    public void requestMentorship(MentorshipRequestDto dto) {
        try {
            mentorshipRequestService.requestMentorship(dto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/mentorship/request/list")
    public Optional<MentorshipRequest> getRequests(RequestFilterDto filter) {
        try {
            return mentorshipRequestService.getRequests(filter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("mentorship/request/{id}/accept")
    public void acceptRequest(long id){
        try {
            mentorshipRequestService.acceptRequest(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/mentorship/request/{id}/reject")
    public void rejectRequest(long id, RejectionDto rejection){
        try {
            mentorshipRequestService.rejectRequest(id, rejection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
