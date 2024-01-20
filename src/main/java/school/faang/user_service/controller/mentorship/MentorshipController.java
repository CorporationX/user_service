package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    public List<UserDto> getMentees(long userId) {
        validationOfInputData(userId);
        return mentorshipService.getMentees(userId);
    }

    public List<UserDto> getMentors(long userId) {
        validationOfInputData(userId);
        return mentorshipService.getMentors(userId);
    }

    public void deleteMentee(long menteeId, long mentorId){
        validationOfInputData(menteeId);
        validationOfInputData(mentorId);
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    public void deleteMentor(long menteeId, long mentorId){
        validationOfInputData(menteeId);
        validationOfInputData(mentorId);
        mentorshipService.deleteMentor(menteeId, mentorId);
    }

    public void validationOfInputData(long id){
        if (id < 1) {
            throw new IllegalArgumentException("Incorrect id entered");
        }
    }
}
