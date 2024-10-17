package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.model.dto.MentorshipRequestDto;
import school.faang.user_service.model.dto.RejectionDto;
import school.faang.user_service.model.filter_dto.MentorshipRequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@RestController
@RequestMapping("/mentorship/request")
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/create")
    public MentorshipRequestDto requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    @GetMapping("/filter")
    public List<MentorshipRequestDto> getRequests(MentorshipRequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    @PutMapping("/accept")
    public MentorshipRequestDto acceptRequest(@RequestParam Long id) {
        return mentorshipRequestService.acceptRequest(id);
    }

    @PutMapping("/reject")
    public MentorshipRequestDto rejectRequest(@RequestParam long id, @RequestBody RejectionDto rejection) {
        return mentorshipRequestService.rejectRequest(id, rejection);
    }
}