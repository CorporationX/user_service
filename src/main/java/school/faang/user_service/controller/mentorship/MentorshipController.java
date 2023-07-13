package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;


@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    public List<UserDTO> getMentees(long mentorId) {
        if (mentorId <= 0) {
            throw new RuntimeException("Invalid request: bad id");
        }
        return mentorshipService.getMentees(mentorId);
    }
}
