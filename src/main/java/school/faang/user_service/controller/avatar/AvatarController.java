package school.faang.user_service.controller.avatar;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.avatar.AvatarService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AvatarController {
    private final AvatarService service;

    @PostMapping("/avatar/default/{userId}")
    public void  createDefaultAvatarForUser(@PathVariable("userId") @Min(1) long userId) {
        service.createDefaultAvatarForUser(userId);
    }
}
