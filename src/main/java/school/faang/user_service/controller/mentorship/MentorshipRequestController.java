package school.faang.user_service.controller.mentorship;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship/requests")
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping
    public MentorshipRequestDto requestMentorship(@Valid @RequestBody MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    @PutMapping("/accept/{id}")
    public void acceptRequest(@PathVariable("id") long id) {
        mentorshipRequestService.acceptRequest(id);
    }

    @PutMapping("/reject/{id}")
    public void rejectRequest(@PathVariable("id") long id, @Valid @RequestBody RejectionDto rejection) {
        mentorshipRequestService.rejectRequest(id, rejection);
    }

    @GetMapping
    public List<MentorshipRequestDto> findAll(@RequestBody RequestFilterDto requestFilterDto) {
        return mentorshipRequestService.findAll(requestFilterDto);
    }
}
