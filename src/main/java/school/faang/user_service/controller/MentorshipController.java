package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipDeleteDto;
import school.faang.user_service.dto.SuccessResponseDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship")
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @GetMapping("/get_mentees")
    public List<UserDto> getMentees(Long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    @GetMapping("/get_mentors")
    public List<UserDto> getMentors(Long menteeId) {
        return mentorshipService.getMentors(menteeId);
    }

    @PostMapping("/delete_mentee")
    public SuccessResponseDto deleteMentee(@RequestBody @Valid MentorshipDeleteDto mentorshipDeleteDto) {
        return mentorshipService.deleteMentee(mentorshipDeleteDto);
    }

    @PostMapping("/delete_mentor")
    public SuccessResponseDto deleteMentor(@RequestBody @Valid MentorshipDeleteDto mentorshipDeleteDto) {
        return mentorshipService.deleteMentor(mentorshipDeleteDto);
    }
}
