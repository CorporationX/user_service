package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRejectionDto;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipRequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/mentorship_request")
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/create")
    public MentorshipRequestDto requestMentorship(@Valid @RequestBody MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    @PostMapping("/get")
    public List<MentorshipRequestDto> getRequests(@RequestBody MentorshipRequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    @PostMapping("/accept/{id}")
    public MentorshipRequestDto acceptRequest(@PathVariable @Positive long id) {
        return mentorshipRequestService.acceptRequest(id);
    }

    @PostMapping("/reject/{id}")
    public MentorshipRequestDto rejectRequest(
            @PathVariable @Positive long id,
            @Valid @RequestBody MentorshipRejectionDto rejection) {
        return mentorshipRequestService.rejectRequest(id, rejection);
    }
}
