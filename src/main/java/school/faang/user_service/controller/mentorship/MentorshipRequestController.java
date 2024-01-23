package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RequestFilterDro;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship")
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;
    private final MentorshipRequestValidator mentorshipRequestValidator;

    @PostMapping("/request")
    public void requestMentorship(MentorshipRequestDto requestDto) {
        mentorshipRequestValidator.validateDescription(requestDto);
        mentorshipRequestService.requestMentorship(requestDto);
    }

    @PostMapping("/requests")
    public List<MentorshipRequestDto> getRequests(RequestFilterDro filters) {
        return mentorshipRequestService.getRequests(filters);
    }
}
