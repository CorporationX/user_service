package school.faang.user_service.controller;

import static school.faang.user_service.constants.InfoMessages.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentor.MentorshipRequestDto;
import school.faang.user_service.dto.mentor.RequestFilterDto;
import school.faang.user_service.dto.mentor.RejectionDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship")
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/requestMentorship")
    public void requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        log.info(START_MENTORSHIP_REQUEST, mentorshipRequestDto.getId());
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        log.info(END_MENTORSHIP_REQUEST, mentorshipRequestDto.getId());
    }

    @GetMapping("/getRequests")
    public List<RequestFilterDto> getRequests(RequestFilterDto filter) {
        log.info(START_GETS_REQUEST);
        List<RequestFilterDto> requestFilterDtoList = mentorshipRequestService.getRequests(filter);
        log.info(END_GETS_REQUEST);
        return requestFilterDtoList;
    }

    @PutMapping("/acceptRequest/{id}")
    public void acceptRequest(@PathVariable long id) {
        log.info(START_ACCEPT_REQUEST, id);
        mentorshipRequestService.acceptRequest(id);
        log.info(END_ACCEPT_REQUEST, id);
    }

    @PutMapping("/rejectRequest/{id}")
    public void rejectRequest(@PathVariable long id, @RequestBody RejectionDto rejection) {
        log.info(START_REJECT_REQUEST, id);
        mentorshipRequestService.rejectRequest(id, rejection);
        log.info(END_REJECT_REQUEST, id);
    }
}