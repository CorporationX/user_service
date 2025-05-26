package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship_request.MentorshipResponseDto;
import school.faang.user_service.dto.mentorship_request.RejectionDto;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorshiprequest")
public class MentorshipRequestController {
    private final MentorshipRequestService service;

    @PostMapping
    public MentorshipResponseDto requestMentorship(@Valid @RequestBody MentorshipRequestDto request) {
        return service.requestMentorship(request);
    }

    @GetMapping
    public List<MentorshipResponseDto> getRequests(@RequestBody MentorshipRequestFilterDto filter) {
        return service.getRequests(filter);
    }

    @PutMapping(value = "/accept/{id}")
    public void acceptRequest(@PathVariable("id") Long id) {
        service.acceptRequest(id);
    }

    @PutMapping(value = "/reject/{id}")
    public void rejectRequest(@PathVariable("id") Long id, @RequestBody RejectionDto rejection) {
        service.rejectRequest(id, rejection);
    }
}
