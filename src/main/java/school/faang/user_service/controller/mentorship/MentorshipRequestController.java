package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

@RestController
@RequestMapping("mentorship/request")
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;
    private final MentorshipRequestMapper mentorshipRequestMapper;

    @PostMapping
    public ResponseEntity<MentorshipRequestDto> requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        if(mentorshipRequestDto.getDescription().equals("I'm a teapot")) {
            return ResponseEntity.status(418).body(mentorshipRequestDto);
        }
        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(mentorshipRequestDto);

        try {
            MentorshipRequest mentorshipRequestResponse = mentorshipRequestService.requestMentorship(mentorshipRequest);
            MentorshipRequestDto mentorshipRequestResponseDto = mentorshipRequestMapper.toDto(mentorshipRequestResponse);
            return ResponseEntity.ok(mentorshipRequestResponseDto);
        } catch (Exception e) {
            mentorshipRequestDto.setRejectionReason(e.getMessage());
            return ResponseEntity.badRequest().body(mentorshipRequestDto);
        }
    }
}
