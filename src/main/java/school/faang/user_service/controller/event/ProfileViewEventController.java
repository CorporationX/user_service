package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.ProfileViewEventDTO;
import school.faang.user_service.service.event.ProfileViewEventPublisher;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileViewEventController {

    private final ProfileViewEventPublisher profileViewEventPublisher;

    @PostMapping("/view")
    public void viewProfile(@RequestBody ProfileViewEventDTO profileViewEventDTO) {
        profileViewEventPublisher.publishProfileViewEvent(profileViewEventDTO);
    }
}