package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship_requests")
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping
    public ResponseEntity<MentorshipRequestDto> requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getDescription() == null || mentorshipRequestDto.getDescription().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(mentorshipRequestService.requestMentorship(mentorshipRequestDto));
    }

    @GetMapping
    public ResponseEntity<List<MentorshipRequestDto>> getRequests(@RequestParam(required = false) String descriptionPattern,
                                                                  @RequestParam(required = false) Long requesterId,
                                                                  @RequestParam(required = false) Long receiverId,
                                                                  @RequestParam(required = false) RequestStatus requestStatus) {
        MentorshipRequestFilterDto mentorshipRequestDto
                = new MentorshipRequestFilterDto(descriptionPattern, requesterId, receiverId, requestStatus);

        return ResponseEntity.ok(mentorshipRequestService.getRequests(mentorshipRequestDto));
    }

    @PatchMapping("/accept/{id}")
    public ResponseEntity<MentorshipRequestDto> acceptRequest(@PathVariable long id) {
        return ResponseEntity.ok(mentorshipRequestService.acceptRequest(id));
    }

    @PatchMapping("/reject/{id}")
    public ResponseEntity<MentorshipRequestDto> rejectRequest(@PathVariable long id,
                                                              @RequestBody RejectionDto rejection) {
        String rejectionReason = rejection.getReason();
        if (rejectionReason == null || rejectionReason.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(mentorshipRequestService.rejectRequest(id, rejection));
    }
}
