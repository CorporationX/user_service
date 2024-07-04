package school.faang.user_service.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.exceptions.ValidationException;
import school.faang.user_service.service.MentorshipRequestService;


@RestController
@Component
@RequestMapping("/api/mentorship-request")
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;
    @PostMapping(value ="/create")
    @ResponseBody
    public MentorshipRequestDto requestMentorship(@Valid @RequestBody MentorshipRequestDto mentorshipRequestDto) {
        if(mentorshipRequestDto.getReceiverId() == mentorshipRequestDto.getRequesterId()) {
            throw new ValidationException("You cannot send request to yourself!");
        }
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }
}
