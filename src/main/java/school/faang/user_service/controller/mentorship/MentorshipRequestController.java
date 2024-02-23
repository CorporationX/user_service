package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.filter.RequestFilterDto;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship/request")
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;
    private final MentorshipRequestValidator mentorshipRequestValidator;

    private List<MentorshipRequestDto> getRequests(RequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    private MentorshipRequestDto acceptRequest(long id) {
        return mentorshipRequestService.acceptRequest(id);
    }

    private MentorshipRequestDto rejectRequest(long id, RejectionDto rejection) {
        if (StringUtils.isEmpty(rejection.getReason())) {
            throw new EntityNotFoundException("There is no description in RejectionDto");
        }
        return mentorshipRequestService.rejectRequest(id, rejection);
    }

    @PostMapping()
    private MentorshipRequestDto requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestValidator.commonCheck(mentorshipRequestDto);
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }
}