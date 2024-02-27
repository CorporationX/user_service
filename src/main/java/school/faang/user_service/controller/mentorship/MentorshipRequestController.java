package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.filter.RequestFilterDto;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.service.mentorship.MentorshipRequestService;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;

import java.util.List;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;
    private final MentorshipRequestValidator mentorshipRequestValidator;

    @GetMapping
    private List<MentorshipRequestDto> getRequests(@RequestBody RequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    @PostMapping("/accept/{id}")
    private MentorshipRequestDto acceptRequest(@PathVariable long id) {
        return mentorshipRequestService.acceptRequest(id);
    }

    @PostMapping("/reject")
    private MentorshipRequestDto rejectRequest(@PathVariable long id, @RequestBody RejectionDto rejection) {
        if (StringUtils.isEmpty(rejection.getReason())) {
            throw new EntityNotFoundException("There is no description in RejectionDto");
        }
        return mentorshipRequestService.rejectRequest(id, rejection);
    }

    @PostMapping("/request")
    private MentorshipRequestDto requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestValidator.commonCheck(mentorshipRequestDto);
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }
}