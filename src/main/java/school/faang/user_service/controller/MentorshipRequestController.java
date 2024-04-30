package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipResponseDto;
import school.faang.user_service.dto.MentorshipRequestFilterDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.util.MentorshipValidator;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship")
public class MentorshipRequestController {


    private final MentorshipRequestService mentorshipRequestService;
    private final MentorshipValidator validator;

    @PostMapping("/request")
    @ResponseStatus(HttpStatus.CREATED)
    public MentorshipResponseDto requestMentorship(@RequestBody MentorshipRequestDto dto) {
        validator.validateRequest(dto);
        return mentorshipRequestService.requestMentorship(dto);
    }

    @GetMapping("/filter")
    public List<MentorshipRequestDto> getRequests(@RequestBody MentorshipRequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    //Не придумал чем заменить глагол "accept"
    @PostMapping("/request/accept/{id}")
    public MentorshipResponseDto acceptRequest(@PathVariable("id") long id) {
        return mentorshipRequestService.acceptRequest(id);
    }

    //Не придумал чем заменить глагол "reject"
    @PostMapping("/request/reject/{id}")
    public MentorshipResponseDto rejectRequest(@PathVariable("id") long id, @RequestBody RejectionDto rejection) {
        validator.validateRejectionDto(rejection);
        return mentorshipRequestService.rejectRequest(id, rejection);
    }
}
