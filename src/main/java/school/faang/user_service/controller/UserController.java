package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.eventService.EventService;
import school.faang.user_service.service.mentorshipService.MentorshipService;
import school.faang.user_service.service.mentorshipService.MentorshipServiceImpl;
import school.faang.user_service.service.userService.UserService;

@RestController
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    private final MentorshipService mentorshipServiceService;
    private final EventService eventService;

    @PostMapping("deactivate/{userId}")
    public void deactivateProfile(@PathVariable long userId) {
        userService.deactivateUser(userId);
        mentorshipServiceService.deactivateMentorship(userId);
        eventService.deleteEventByUserId(userId);
    }
}
