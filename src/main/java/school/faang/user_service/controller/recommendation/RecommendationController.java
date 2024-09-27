package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final RecommendationMapper recommendationMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecommendationDto giveRecommendation(@RequestBody @Valid RecommendationDto recommendationDto) {
        return recommendationService.createRecommendation(recommendationDto);
    }

    @PatchMapping
    public RecommendationDto updateRecommendation(@RequestBody @Valid RecommendationDto recommendationDto) {
        return recommendationService.updateRecommendation(recommendationDto);
    }

    @DeleteMapping("/{recommendationId}")
    public void deleteRecommendation(@PathVariable long recommendationId) {
        recommendationService.deleteRecommendation(recommendationId);
    }

    @GetMapping("/receiver/{receiverId}")
    public List<RecommendationDto> getAllUserRecommendations(@PathVariable long receiverId,
                                                             @RequestParam int offset,
                                                             @RequestParam int limit) {
        List<Recommendation> allUserRecommendations = recommendationService.getAllUserRecommendations(receiverId, offset, limit);

        return recommendationMapper.toDtoList(allUserRecommendations);
    }

    @GetMapping("/author/{authorId}")
    public List<RecommendationDto> getAllGivenRecommendations(@PathVariable long authorId,
                                                              @RequestParam int offset,
                                                              @RequestParam int limit) {
        List<Recommendation> allGivenRecommendations = recommendationService.getAllGivenRecommendations(authorId, offset, limit);

        return recommendationMapper.toDtoList(allGivenRecommendations);
    }
}
