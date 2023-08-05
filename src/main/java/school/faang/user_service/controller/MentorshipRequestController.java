package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@RestController
@RequestMapping("/mentorship/requests")
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/")
    public void requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        log.info("Endpoint <requestMentorship>, uri='/mentorship/requests' was called successfully");
        if (mentorshipRequestDto.getDescription() == null || mentorshipRequestDto.getDescription().isBlank()) {
            throw new DataValidationException("Add a description to your mentoring request");
        }
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    @PostMapping("/get-by-filters")
    public List<MentorshipRequestDto> getRequests(@RequestBody RequestFilterDto filter) {
        log.info("Endpoint <getRequests>, uri='/mentorship/requests/get-by-filters was called successfully");
        return mentorshipRequestService.getRequests(filter);
    }

    @PutMapping("/{id}/accept")
    public void acceptRequest(@PathVariable long id) {
        log.info("Endpoint <acceptRequest>, uri='/mentorship/requests/{}/accept' was called successfully", id);
        mentorshipRequestService.acceptRequest(id);
    }

    @PostMapping("/{id}/reject")
    public void rejectRequest(@PathVariable long id, @RequestBody RejectionDto rejection) {
        log.info("Endpoint <rejectRequest>, uri='/mentorship/requests/{}/reject' was called successfully", id);
        mentorshipRequestService.rejectRequest(id, rejection);
    }
}