package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import school.faang.user_service.entity.dto.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
@Slf4j
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @GetMapping("/mentors/{mentorId}/mentees")
    public List<UserDto> getMentees(@PathVariable("mentorId") long mentorId) {
        log.info("Received request to get mentees for mentor with ID: {}", mentorId);
        List<UserDto> mentees = mentorshipService.getMentees(mentorId);
        log.info("Found {} mentees for mentor with ID: {}", mentees.size(), mentorId);
        return mentees;
    }

    @GetMapping("/mentors/{menteeId}/mentors")
    public List<UserDto> getMentors(@PathVariable("menteeId") long menteeId) {
        log.info("Received request to get mentors for mentee with ID: {}", menteeId);
        List<UserDto> mentors = mentorshipService.getMentors(menteeId);
        log.info("Found {} mentors for mentee with ID: {}", mentors.size(), menteeId);
        return mentors;
    }

    @DeleteMapping("/mentees/{menteeId}/mentors/{mentorId}")
    public void deleteMentees(@PathVariable("menteeId") long menteeId,
                              @PathVariable("mentorId") long mentorId) {
        log.info("Received request to delete mentor-mentee relationship for mentor ID: {} and mentee ID: {}", mentorId, menteeId);
        mentorshipService.deleteMentee(menteeId, mentorId);
        log.info("Deleted mentor-mentee relationship for mentor ID: {} and mentee ID: {}", mentorId, menteeId);
    }

    @DeleteMapping("/mentors/{mentorId}/mentees/{menteeId}")
    public void deleteMentors(@PathVariable("mentorId") long mentorId,
                              @PathVariable("menteeId") long menteeId) {
        log.info("Received request to delete mentee-mentor relationship for mentor ID: {} and mentee ID: {}", mentorId, menteeId);
        mentorshipService.deleteMentor(mentorId, menteeId);
        log.info("Deleted mentee-mentor relationship for mentor ID: {} and mentee ID: {}", mentorId, menteeId);
    }
}
