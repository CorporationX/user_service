package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.event.RecommendationEvent;
import school.faang.user_service.publisher.RecommendationEventPublisher;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendations")
@Validated
@Slf4j
public class RecommendationController {

    private final RecommendationEventPublisher recommendationEventPublisher;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void publishRecommendation(@Validated @RequestBody RecommendationEvent recommendationEvent) {
        log.info("Received a recommendation to publish: {}", recommendationEvent);

        if (recommendationEvent.getCreatedAt() == null) {
            recommendationEvent.setCreatedAt(LocalDateTime.now());
        }
        recommendationEventPublisher.publishToRecommendation(recommendationEvent);
    }
}