package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.controller.ApiPath;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.RECOMMENDATION)
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping()
    private ResponseEntity<RecommendationDto> giveRecommendation(@Valid @RequestBody RecommendationDto recommendationDto){
        return ResponseEntity.status(HttpStatus.OK).body(recommendationService.create(recommendationDto));

    }
}
