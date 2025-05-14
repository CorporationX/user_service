package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.mentorship.GetMenteesResponse;
import school.faang.user_service.dto.mentorship.GetMentorsResponse;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@Controller
@RequiredArgsConstructor
    public class MentorshipController {

    private final MentorshipService mentorshipService;

    public List<GetMenteesResponse> getMentees(long id) {
        return mentorshipService.getMentees(id);
    }

    public List<GetMentorsResponse> getMentors(long id) {
        return mentorshipService.getMentors(id);
    }

    public void deleteMentee(long menteeId, long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}