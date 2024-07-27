package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.mentorship.AcceptMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectRequestDto;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.List;


@RestController
@RequestMapping("/mentorship/request")
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    @PostMapping("/create")
    public MentorshipRequestDto requestMentorship(@Valid @RequestBody MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestValidator.isIdsEqual(mentorshipRequestDto.getReceiverId(),mentorshipRequestDto.getRequesterId());
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    @PostMapping( "/accept")
    public void acceptRequest(@Valid @RequestBody AcceptMentorshipRequestDto acceptMentorshipRequestDto) {
        mentorshipRequestValidator.isIdsEqual(acceptMentorshipRequestDto.getReceiverId(),acceptMentorshipRequestDto.getRequesterId());
        mentorshipRequestService.acceptRequest(acceptMentorshipRequestDto);
    }

    @PostMapping( "/reject")
    public void rejectRequest(@Valid @RequestBody RejectRequestDto rejectRequestDto) {
        mentorshipRequestService.rejectRequest(rejectRequestDto);
    }
    @PostMapping("/all")
    public ResponseEntity<List<MentorshipRequestDto>> getAllMentorshipRequests(@RequestBody MentorshipRequestFilterDto filterDto) {
        return ResponseEntity.status(200).body(mentorshipRequestService.getAllMentorshipRequests(filterDto));
    }
}
