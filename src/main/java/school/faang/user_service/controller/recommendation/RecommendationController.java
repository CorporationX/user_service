package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.Recommendation;
import school.faang.user_service.service.recommendation.RecommendationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendation")
// TODO контроллер заглушка
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping("/")
    public ResponseEntity<String> add(@RequestBody Recommendation recommendation) {
        recommendationService.recommendUser(recommendation.getReceiveId());
        return ResponseEntity.ok().body("ok");
    }

    @GetMapping("/{id}")
    public Recommendation getRecommendation(@PathVariable long id) {
        // TODO
        return new Recommendation(1L, 1L, "you super good!");
    }
}
