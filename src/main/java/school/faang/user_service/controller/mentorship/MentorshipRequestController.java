package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorshipRequest")
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;
    @PostMapping("/create")
    public void requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }
    @GetMapping("/get")
    public List<MentorshipRequestDto> getRequest(@RequestBody RequestFilterDto requestFilterDto){
        return mentorshipRequestService.getRequest(requestFilterDto);
    }
    @PutMapping("/accept/{id}")
    public MentorshipRequestDto acceptRequest(@PathVariable long id) {
        return mentorshipRequestService.acceptRequest(id);
    }
    @PutMapping("/reject/{id}")
    public MentorshipRequestDto rejectRequest(@PathVariable long id,@RequestBody RejectionDto rejection){
        return mentorshipRequestService.rejectRequest(id,rejection);
    }
}
