package school.faang.user_service.controller.mentorship;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship-requests")
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping()
    public ResponseEntity<MentorshipRequestDto> requestMentorship(@Valid @RequestBody MentorshipRequestDto mentorshipRequestDto) {
        MentorshipRequestDto result = mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping()
    public ResponseEntity<List<MentorshipRequestDto>> getRequests(@Valid @RequestBody RequestFilterDto requestFilterDto) {
        List<MentorshipRequestDto> mentorshipRequestDto = mentorshipRequestService.getRequests(requestFilterDto);
        return ResponseEntity.ok(mentorshipRequestDto);
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<MentorshipRequestDto> acceptMentorship(@PathVariable Long id) {
        MentorshipRequestDto result = mentorshipRequestService.acceptMentorship(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<MentorshipRequestDto> rejectRequest(@Valid @PathVariable long id, @RequestBody RejectionDto rejection) {
        MentorshipRequestDto result = mentorshipRequestService.rejectRequest(id, rejection);
        return ResponseEntity.ok(result);
    }
}
