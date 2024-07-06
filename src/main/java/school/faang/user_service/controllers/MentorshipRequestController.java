package school.faang.user_service.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.AcceptMentorshipRequestDto;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.exceptions.ValidationException;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.Map;


@RestController
@Component
@RequestMapping("/api/mentorship-request")
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping(value = "/create")
    @ResponseBody
    public MentorshipRequestDto requestMentorship(@Valid @RequestBody MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getReceiverId() == mentorshipRequestDto.getRequesterId()) {
            throw new ValidationException("You cannot send request to yourself!");
        }
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    @PostMapping(value = "/accept")
    public ResponseEntity<Map<String, String>> acceptRequest(@Valid @RequestBody AcceptMentorshipRequestDto acceptMentorshipRequestDto) {
        if (acceptMentorshipRequestDto.getReceiverId() == acceptMentorshipRequestDto.getRequesterId()) {
            throw new ValidationException("You cannot accept request to yourself!");
        }
        return mentorshipRequestService.acceptRequest(acceptMentorshipRequestDto);
    }

    @PostMapping(value = "/reject/{id}")
    public ResponseEntity<Map<String, String>> rejectRequest(@PathVariable Long id, @RequestBody String reason) {
        return mentorshipRequestService.rejectRequest(id,reason);
    }
}
