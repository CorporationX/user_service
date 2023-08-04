package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.mentorshiprequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshiprequest.RejectionDto;
import school.faang.user_service.dto.mentorshiprequest.RequestFilterDto;
import school.faang.user_service.dto.mentorshiprequest.RequestResponse;
import school.faang.user_service.service.MentorshipRequestService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship")
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/send_request")
    @ResponseStatus(HttpStatus.OK)
    public MentorshipRequestDto requestMentorship(@Valid @RequestBody MentorshipRequestDto dto) {
        return mentorshipRequestService.requestMentorship(dto);
    }

    @GetMapping("/requests")
    @ResponseStatus(HttpStatus.OK)
    public RequestResponse getRequests(@Valid @RequestBody RequestFilterDto filter) {
        return new RequestResponse(mentorshipRequestService.getRequests(filter));
    }

    @PutMapping("/accept/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MentorshipRequestDto acceptRequest(@PathVariable("id") long id) {
        return mentorshipRequestService.acceptRequest(id);
    }

    @PutMapping("/reject/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MentorshipRequestDto rejectRequest(@PathVariable("id") long id,
                                              @Valid @RequestBody RejectionDto rejectionDto) {
        return mentorshipRequestService.rejectRequest(id, rejectionDto);
    }
}