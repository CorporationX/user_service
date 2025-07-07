package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping("/create")
    public RecommendationDto create(@RequestBody @Valid CreateRecommendationDto recommendationDto) {
        return recommendationService.create(recommendationDto);
    }

    @PatchMapping("{id}")
    public RecommendationDto update(
            @PathVariable("id") long recommendationId,
            @RequestBody @Valid UpdateRecommendationDto recommendationDto) {
        return recommendationService.update(recommendationId, recommendationDto);
    }

    @PostMapping("/delete")
    public void delete(long recommendationId) {
        recommendationService.delete(recommendationId);
    }

    @PostMapping("/getbyfilters")
    public List<RecommendationDto> getByFilters(@RequestBody @Valid RecommendationFilterDto filters) {
        return recommendationService.getByFilters(filters);
    }

}
