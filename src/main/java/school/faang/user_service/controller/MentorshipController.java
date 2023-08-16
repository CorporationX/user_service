package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
@Slf4j
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @GetMapping("/mentees/{userId}")
    public List<UserDto> getMentees(@PathVariable("userId") long userId) {
        log.info("Endpoint <getMentees>, uri='/mentorship/mentees/{}' was called successfully", userId);
        return mentorshipService.getMentees(userId);
    }

    @GetMapping("/mentors/{userId}")
    public List<UserDto> getMentors(@PathVariable("userId") long userId) {
        log.info("Endpoint <getMentors>, uri='/mentorship/mentors/{}' was called successfully", userId);
        return mentorshipService.getMentors(userId);
    }

    @DeleteMapping("/mentee")
    public void deleteMentee(@RequestParam("menteeId") long menteeId, @RequestParam("mentorId") long mentorId) {
        log.info("Endpoint <deleteMentee>, uri='/mentorship/mentee' with param: menteeId={}, mentorId={} was called successfully", menteeId, mentorId);
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @DeleteMapping("/mentor")
    public void deleteMentor(@RequestParam("menteeId") long menteeId, @RequestParam("mentorId") long mentorId) {
        log.info("Endpoint <deleteMentor>, uri='/mentorship/mentor' with param: menteeId={}, mentorId={} was called successfully", menteeId, mentorId);
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}