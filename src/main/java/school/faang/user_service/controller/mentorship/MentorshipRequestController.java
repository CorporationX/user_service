package school.faang.user_service.controller.mentorship;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MentorshipFilterDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipResponseDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("mentorship/requests")
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping
    public ResponseEntity<MentorshipResponseDto> requestMentorship(
            @RequestBody @Valid MentorshipRequestDto mentorshipRequestDto) {
        MentorshipResponseDto response = mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MentorshipResponseDto>> getMentorshipRequests(
            @ModelAttribute MentorshipFilterDto filterDto) {
        List<MentorshipResponseDto> response = mentorshipRequestService.getRequests(filterDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> acceptRequest(@PathVariable Long requestId) {
        mentorshipRequestService.acceptRequest(requestId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectRequest(@PathVariable Long id,
                                              @RequestBody RejectionDto dto) {
        mentorshipRequestService.rejectRequest(id, dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}