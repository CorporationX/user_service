package school.faang.user_service.controller;

import static school.faang.user_service.constants.InfoMessages.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.mentor.MentorshipRequestDto;
import school.faang.user_service.dto.mentor.RequestFilterDto;
import school.faang.user_service.dto.mentor.RejectionDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        log.info(START_MENTORSHIP_REQUEST, mentorshipRequestDto.getId());
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        log.info(END_MENTORSHIP_REQUEST, mentorshipRequestDto.getId());
    }

    public List<RequestFilterDto> getRequests(RequestFilterDto filter) {
        log.info(START_GETS_REQUEST);
        List<RequestFilterDto> requestFilterDtoList = mentorshipRequestService.getRequests(filter);
        log.info(END_GETS_REQUEST);
        return requestFilterDtoList;
    }

    public void acceptRequest(long id) {
        log.info(START_ACCEPT_REQUEST, id);
        mentorshipRequestService.acceptRequest(id);
        log.info(END_ACCEPT_REQUEST, id);
    }

    public void rejectRequest(long id, RejectionDto rejection) {
        log.info(START_REJECT_REQUEST, id);
        mentorshipRequestService.rejectRequest(id, rejection);
        log.info(END_REJECT_REQUEST, id);
    }
}
