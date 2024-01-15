package school.faang.user_service.controller.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import school.faang.user_service.entity.User;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    public List<UserDTO> getMentees(Long mentorId) {
        validationOfInputData(mentorId);
        return mentorshipService.getMenteesOfUser(mentorId);
    }

    public List<UserDTO> getMentors(Long menteeId) {
        validationOfInputData(menteeId);
        return mentorshipService.getMentorsOfUser(menteeId);
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

    public void validationOfInputData(Long id){
        if (id == null || id < 1) {
            throw new EntityNotFoundException("Incorrect id entered");
        }
    }
}
