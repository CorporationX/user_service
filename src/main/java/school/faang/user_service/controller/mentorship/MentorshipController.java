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
        if (mentorId == null || mentorId < 1) {
            throw new EntityNotFoundException("Incorrect id entered");
        }
        return mentorshipService.getMenteesOfUser(mentorId);
    }
}
