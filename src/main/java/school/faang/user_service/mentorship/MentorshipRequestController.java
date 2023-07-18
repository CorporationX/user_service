package school.faang.user_service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
    //не понял, как и где использовать маппер т.к. не понимаю что и как мы получаем
    // из RequestBody
    public void requestMentorship(@RequestBody MentorshipRequestDto dto) {
        try {
            mentorshipRequestService.requestMentorship(dto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/mentorship/request/list")
    public Optional<MentorshipRequest> getRequests(@RequestBody RequestFilterDto filter) {
        //не совсем понимаю как это правильно реализовать,
        // из-за того, как сделать запрос в бд по фильтрам
        try {
            return mentorshipRequestService.getRequests(filter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("mentorship/request/{id}/accept")
    public void acceptRequest(@PathVariable long id){
        try {
            mentorshipRequestService.acceptRequest(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/mentorship/request/{id}/reject")
    public void rejectRequest(@PathVariable long id, RejectionDto rejection){
        try {
            mentorshipRequestService.rejectRequest(id, rejection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
