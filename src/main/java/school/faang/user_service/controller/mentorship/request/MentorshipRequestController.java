package school.faang.user_service.controller.mentorship.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.service.mentorship.request.MentorshipRequestService;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/mentorship")
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;
    private final MentorshipRequestValidator mentorshipRequestValidator;

    @PostMapping
    public ResponseEntity<String> requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestValidator.validateRequestMentorshipDescription(mentorshipRequestDto)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Description is required");
        }

        try {
            mentorshipRequestService.requestMentorship(mentorshipRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Mentorship request created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating mentorship request: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> acceptRequest(@PathVariable long id) {
        mentorshipRequestService.acceptRequest(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<MentorshipRequestDto>> getRequests(RequestFilterDto filter) {
        List<MentorshipRequestDto> requests = mentorshipRequestService.getRequests(filter);
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectRequest(@PathVariable long id, @RequestBody RejectionDto rejection) {
        mentorshipRequestService.rejectRequest(id, rejection);
        return ResponseEntity.ok().build();
    }

}
