package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.validator.mentorship.MentorshipValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/Mentorship")
public class MentorshipController {
    private final MentorshipService mentorshipService;
    private final MentorshipValidator mentorshipValidator;

    @GetMapping("/{id}")
    public List<UserDto> getMentees(@PathVariable long id) {
        mentorshipValidator.idValidator(id);
        return mentorshipService.getMentees(id);
    }

    @GetMapping("/{id}")
    public List<UserDto> getMentors(@PathVariable long id) {
        mentorshipValidator.idValidator(id);
        return mentorshipService.getMentors(id);
    }
}
