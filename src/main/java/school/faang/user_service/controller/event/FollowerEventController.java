package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.FollowerEventDTO;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.FollowerEvent;
import school.faang.user_service.service.event.FollowerEventPublisher;

@RestController
@RequestMapping("/api/v1/follower-events")
@RequiredArgsConstructor
public class FollowerEventController {

    private final FollowerEventPublisher followerEventPublisher;

    @PostMapping("/publish-follow")
    public void publishFollowEvent(@RequestParam(name = "followerId") Long followerId,
                                   @RequestParam(name = "followeeId") Long followeeId,
                                   @RequestParam(name = "projectId", required = false) Long projectId) {

        if (followerId == null || followeeId == null) {
            throw new DataValidationException("followerId and followeeId cannot be null");
        }

        FollowerEvent followerEvent = new FollowerEvent(
                followerId,
                followeeId,
                projectId != null ? projectId : 0L,
                null
        );

        followerEventPublisher.publish(followerEvent);

    }

    @PostMapping("/publish-unfollow")
    public void publishUnfollowEvent(@RequestParam(name = "followerId") Long followerId,
                                     @RequestParam(name = "followeeId") Long followeeId,
                                     @RequestParam(name = "projectId", required = false) Long projectId) {

        FollowerEventDTO followerEventDTO = new FollowerEventDTO(
                followerId,
                followeeId,
                projectId != null ? projectId : 0L,
                null
        );
    }
}