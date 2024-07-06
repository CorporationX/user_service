package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@RestController
@RequestMapping("mentorship/request")
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;
    private final MentorshipRequestMapper mentorshipRequestMapper;

    @GetMapping
    public ResponseEntity<?> getRequests(RequestFilterDto filter) {
        System.out.println(filter);
        try {
            return ResponseEntity.ok(mentorshipRequestService.getRequests(filter));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "accept")
    public ResponseEntity<?> acceptRequest(@RequestParam long id) {
        try {
            MentorshipRequest mentorshipRequestResponse = mentorshipRequestService.acceptRequest(id);
            MentorshipRequestDto mentorshipRequestResponseDto = mentorshipRequestMapper.toDto(mentorshipRequestResponse);
            return ResponseEntity.ok(mentorshipRequestResponseDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(path = "reject")
    public ResponseEntity<?> rejectRequest(@RequestParam long id, @RequestBody RejectionDto rejection) {
        try {
            MentorshipRequest mentorshipRequestResponse = mentorshipRequestService.rejectRequest(id, rejection);
            MentorshipRequestDto mentorshipRequestResponseDto = mentorshipRequestMapper.toDto(mentorshipRequestResponse);
            return ResponseEntity.ok(mentorshipRequestResponseDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(mentorshipRequestDto);

        try {
            MentorshipRequest mentorshipRequestResponse = mentorshipRequestService.requestMentorship(mentorshipRequest);
            MentorshipRequestDto mentorshipRequestResponseDto = mentorshipRequestMapper.toDto(mentorshipRequestResponse);
            return ResponseEntity.ok(mentorshipRequestResponseDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
