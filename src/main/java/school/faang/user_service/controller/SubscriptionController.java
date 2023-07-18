package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.SubscriptionDto;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.validator.SubscriptionValidator;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscription")
public class SubscriptionController {
    private final SubscriptionService service;
    private final SubscriptionValidator validator;

    @PostMapping("/follow")
    public ResponseEntity<?> followUser(@RequestBody SubscriptionDto dto) {
        validator.validateId(dto.getFollowerId(), dto.getFolloweeId());
        service.followUser(dto.getFollowerId(), dto.getFolloweeId());
        return ResponseEntity.ok().body("Followed");
    }

    @DeleteMapping("/unfollow")
    public ResponseEntity<?> unfollowUser(@RequestBody SubscriptionDto dto) {
        validator.validateId(dto.getFollowerId(), dto.getFolloweeId());
        service.unfollowUser(dto.getFollowerId(), dto.getFolloweeId());
        return ResponseEntity.ok().body("Unfollowed");
    }
}
