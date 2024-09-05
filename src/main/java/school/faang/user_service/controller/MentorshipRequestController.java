package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorshipRequest.RejectionDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mentorship/request")
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/create")
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    @GetMapping("/filter")
    public List<MentorshipRequestDto> getRequests(MentorshipRequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    @PutMapping("/accept")
    public MentorshipRequestDto acceptRequest(Long id) {
        return mentorshipRequestService.acceptRequest (id);
    }

    @PutMapping("/reject")
    public MentorshipRequestDto rejectRequest(long id, RejectionDto rejection) {
        return mentorshipRequestService.rejectRequest(id, rejection);
    }
}