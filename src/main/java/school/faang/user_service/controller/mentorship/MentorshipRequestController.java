package school.faang.user_service.controller.mentorship;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.controller.ApiPath;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.REQUEST_MENTORSHIP)
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping
    public ResponseEntity<MentorshipRequestDto> requestMentorship(@RequestBody @Valid MentorshipRequestDto mentorshipRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mentorshipRequestService.requestMentorship(mentorshipRequestDto));
    }

    @GetMapping
    public ResponseEntity<List<MentorshipRequestDto>> getRequests(@RequestBody(required = false) RequestFilterDto filters) {
        return ResponseEntity.status(HttpStatus.OK).body(mentorshipRequestService.getRequests(filters));
    }
}
