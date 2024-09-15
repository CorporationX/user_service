package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.entity.mentorship.MentorshipStartEvent;
import school.faang.user_service.publisher.mentorship.MentorshipStartPublisher;

@RestController
@RequestMapping("v1/test/mentorship")
@RequiredArgsConstructor
public class MentorshipStartTestController {
    private final MentorshipStartPublisher mentorshipStartPublisher;

    @PostMapping("{mentorId}/{menteeId}")
    public void startMentorship(@PathVariable long mentorId, @PathVariable long menteeId) {
        mentorshipStartPublisher.publish(MentorshipStartEvent.builder().mentorId(mentorId).menteeId(menteeId).build());
    }
}
