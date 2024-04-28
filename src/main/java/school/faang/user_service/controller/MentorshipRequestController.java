package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.service.MentorshipRequestService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MentorshipRequestController{

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping
    public MentorshipRequestDto requestMentorship(@Valid @RequestBody MentorshipRequestDto mentorshipRequestDto){
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    @PostMapping("/accept")
    public void acceptRequest(@RequestParam  long id){
         mentorshipRequestService.acceptRequest(id);
    }

}
