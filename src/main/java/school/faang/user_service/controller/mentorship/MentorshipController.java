package school.faang.user_service.controller.mentorship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import school.faang.user_service.entity.User;

import java.util.List;

@Controller
public class MentorshipController {
    @Autowired
    private MentorshipService mentorshipService;
    @PostMapping("/mentors/{mentorId}/mentees")
    @ResponseBody
    public List<User> getMentees(@PathVariable Long mentorId){
        return mentorshipService.getMentees(mentorId);
    }
}
