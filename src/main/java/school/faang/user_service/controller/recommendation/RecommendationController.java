package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RequestMapping("api/v1/recommendation")
@RestController
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final RecommendationMapper recommendationMapper;

    @PostMapping
    public ResponseEntity<RecommendationDto> giveRecommendation(
            @RequestBody @Valid RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        recommendation = recommendationService.create(recommendation);
        RecommendationDto responseDto = recommendationMapper.toDto(recommendation);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PatchMapping
    public ResponseEntity<RecommendationDto> updateRecommendation(
            @RequestBody @Valid RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        recommendation = recommendationService.update(recommendation);
        RecommendationDto responseDto = recommendationMapper.toDto(recommendation);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecommendation(@PathVariable long id) {
        recommendationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{receiverId}")
    public ResponseEntity<List<RecommendationDto>> getAllUserRecommendations(
            @PathVariable long receiverId) {
        List<Recommendation> recommendations = recommendationService.getAllUserRecommendations(receiverId);
        List<RecommendationDto> recommendationsDto = recommendationMapper.toRecommendationDtoList(recommendations);
        return ResponseEntity.ok(recommendationsDto);
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<RecommendationDto>> getAllGivenRecommendations(
            @PathVariable long authorId) {
        List<Recommendation> recommendations = recommendationService.getAllGivenRecommendations(authorId);
        List<RecommendationDto> recommendationDtoList = recommendationMapper.toRecommendationDtoList(recommendations);
        return ResponseEntity.ok(recommendationDtoList);
    }
}
