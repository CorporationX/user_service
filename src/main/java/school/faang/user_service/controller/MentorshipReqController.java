package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipReqDto;
import school.faang.user_service.service.mentorship.MentorshipReqService;

@RestController
@RequestMapping("/api/v1/mentorship-requests")
@RequiredArgsConstructor
public class MentorshipReqController {

    private final MentorshipReqService mentorshipReqService;

    @PostMapping
    public ResponseEntity<MentorshipReqDto> createRequest(@RequestBody @Valid MentorshipReqDto dto) {
        MentorshipReqDto created = mentorshipReqService.requestMentorship(dto);
        return ResponseEntity.ok(created);

    }
}
