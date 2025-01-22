package school.faang.user_service.controller;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.controller.mentorship.MentorshipServiceImpl;
import school.faang.user_service.entity.User;

import java.util.List;

@Component
public class MentorshipController {
    private MentorshipServiceImpl mentorshipService;

    @Autowired
    public void mentorshipService(MentorshipServiceImpl mentorshipService) {
        this.mentorshipService = mentorshipService;
    }
    @NonNull
    public List<User> getMentees(long userId) {
        return mentorshipService.getMentees(userId);
    }
    public List<User> getMentors(long userId) {
        return mentorshipService.getMentors(userId);
    }
    public void deleteMentee(long menteeId, long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }
    public void deleteMentor(long menteeId, long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
