package school.faang.user_service.controller.mentorship;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.service.MentorshipRequestService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/v1/mentorship_request")
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping
    public MentorshipRequestDto requestMentorship(@Valid @RequestBody MentorshipRequestDto mentorshipRequestDto) {
        log.info("Received mentorship request: {}", mentorshipRequestDto);
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }
}
