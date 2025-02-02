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
import school.faang.user_service.dto.mentorship.MentorshipRequestRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestResponseDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/mentorship/requests")
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping
    public ResponseEntity<MentorshipRequestResponseDto> requestMentorship(@RequestBody MentorshipRequestRequestDto mentorshipRequestRequestDto) {
        String mentorshipRequestDescription = mentorshipRequestRequestDto.description();
        if (mentorshipRequestDescription == null || mentorshipRequestDescription.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(mentorshipRequestService.requestMentorship(mentorshipRequestRequestDto));
    }

    @GetMapping
    public ResponseEntity<List<MentorshipRequestResponseDto>> getRequests(@RequestParam(required = false) String descriptionPattern,
                                                                  @RequestParam(required = false) Long requesterId,
                                                                  @RequestParam(required = false) Long receiverId,
                                                                  @RequestParam(required = false) RequestStatus requestStatus) {
        MentorshipRequestFilterDto mentorshipRequestDto
                = new MentorshipRequestFilterDto(descriptionPattern, requesterId, receiverId, requestStatus);

        return ResponseEntity.ok(mentorshipRequestService.getRequests(mentorshipRequestDto));
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<MentorshipRequestResponseDto> acceptRequest(@PathVariable long id) {
        return ResponseEntity.ok(mentorshipRequestService.acceptRequest(id));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<MentorshipRequestResponseDto> rejectRequest(@PathVariable long id,
                                                                      @RequestBody RejectionDto rejection) {
        String rejectionReason = rejection.reason();
        if (rejectionReason == null || rejectionReason.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(mentorshipRequestService.rejectRequest(id, rejection));
    }
}
