package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.UserSkillGuaranteeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendations")
public class SkillRecommendationController {
    private final UserSkillGuaranteeService userSkillGuaranteeService;

    @PostMapping("/users/{userId}/skills/{skillId}")
    public ResponseEntity<Void> publishSkillAcquiredEvent(@PathVariable long userId, @PathVariable long skillId) {
        userSkillGuaranteeService.publishSkillAcquiredEvent(userId, skillId);
        return ResponseEntity.ok().build();
    }
}
