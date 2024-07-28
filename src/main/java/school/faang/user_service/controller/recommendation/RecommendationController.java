package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.controller.ApiPath;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.RECOMMENDATION)
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping()
    private ResponseEntity<RecommendationDto> giveRecommendation(@Valid @RequestBody RecommendationDto recommendationDto){
        return ResponseEntity.status(HttpStatus.OK).body(recommendationService.create(recommendationDto));

    }

    @PutMapping()
    private ResponseEntity<RecommendationDto> updateRecommendation(@Valid @RequestBody RecommendationDto recommendationDto){
        return ResponseEntity.status(HttpStatus.OK).body(recommendationService.update(recommendationDto));
    }

    @DeleteMapping("/{recommendationId}")
    private ResponseEntity<Void> deleteRecommendation(@PathVariable long recommendationId){
                recommendationService.delete(recommendationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reciever/{recieverId}")
    private ResponseEntity<List<RecommendationDto>> getAllUserRecommendations(@PathVariable long recieverId, @RequestBody Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(recommendationService.getAllUserRecommendations(recieverId, pageable));
    }

    @GetMapping("/author/{authorId}")
    private ResponseEntity<List<RecommendationDto>> getAllGivenRecommendations(@PathVariable long authorId, @RequestBody Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(recommendationService.getAllGivenRecommendations(authorId, pageable));
    }
}
