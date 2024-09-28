package school.faang.user_service.controller.mentorshiprequest;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorshiprequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshiprequest.RejectionDto;
import school.faang.user_service.dto.mentorshiprequest.RequestFilterDto;
import school.faang.user_service.service.mentorshiprequest.MentorshipRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/request")
    public MentorshipRequestDto requestMentorship(@NotNull @RequestBody MentorshipRequestDto request) {
        return mentorshipRequestService.requestMentorship(request);
    }

    @PostMapping("/getRequest")
    public List<MentorshipRequestDto> getRequests(@NotNull @RequestBody RequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    @PutMapping("/acceptRequest")
    public MentorshipRequestDto acceptRequest(@RequestBody long id) {
        return mentorshipRequestService.acceptRequest(id);
    }

    @PostMapping("/rejectRequest")
    public MentorshipRequestDto rejectRequest(@RequestBody long id, @NotNull @RequestBody RejectionDto rejection) {
        return mentorshipRequestService.rejectRequest(id, rejection);
    }
}
