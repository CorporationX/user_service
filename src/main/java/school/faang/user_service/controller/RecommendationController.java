package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping("/recommendation")
    public RecommendationDto giveRecommendation(@RequestBody
                                                @Valid RecommendationDto recommendationDto) {
        return recommendationService.create(recommendationDto);
    }

    @PutMapping("/recommendation/setting")
    public RecommendationDto updateRecommendation(@Valid RecommendationDto recommendationDto) {
        return recommendationService.update(recommendationDto);
    }

    @DeleteMapping("/recommendation/{id}")
    public void deleteRecommendation(@PathVariable long id) {
        recommendationService.delete(id);
    }

    @GetMapping("/receiver/{receiverId}")
    public List<RecommendationDto> getUserAllRecommendations(@PathVariable long receiverId,
                                                             @RequestParam("page") int page,
                                                             @RequestParam("size") int size) {
        return recommendationService.getAllUserRecommendations(receiverId, page, size);
    }

    @GetMapping("/author/{authorId}")
    public List<RecommendationDto> getGivenAllRecommendations(@PathVariable long authorId,
                                                              @RequestParam("page") int page,
                                                              @RequestParam("size") int size) {
        return recommendationService.getAllGivenRecommendations(authorId, page, size);
    }
}