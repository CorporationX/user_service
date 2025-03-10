package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.service.MentorshipRequestService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MentorshipRequestController {
    private static final String START_MENTORSHIP_REQUEST = "Start requestMentorship id: {}";
    private static final String END_MENTORSHIP_REQUEST = "End requestMentorship id: {}";

    private final MentorshipRequestService mentorshipRequestService;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        log.info(START_MENTORSHIP_REQUEST, mentorshipRequestDto.getId());
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        log.info(END_MENTORSHIP_REQUEST, mentorshipRequestDto.getId());
    }
}
