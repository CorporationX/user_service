package school.faang.user_service.controller;

import lombok.Data;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@Data
@Controller
public class MentorshipController {
    MentorshipService mentorshipService;


    public List<UserDto> getMentees(long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    public List<UserDto> getMentors(long menteeId) {
        return mentorshipService.getMentors(menteeId);
    }
    public List<UserDto> deleteMentee(long mentorId, long menteeId) {
        return mentorshipService.deleteMentee(mentorId, menteeId);
    }

    public List<UserDto> deleteMentor(long menteeId, long mentorId) {
        return mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
