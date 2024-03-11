package school.faang.user_service.controller;

import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@RestController
public class MentorshipController {
    MentorshipService mentorshipService;


    public List<UserDto> getMentees(long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    public List<UserDto> getMentors(long menteeId) {
        return mentorshipService.getMentors(menteeId);
    }

    public void deleteMentee(long mentorId, long menteeId) {
        mentorshipService.deleteMentee(mentorId, menteeId);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
