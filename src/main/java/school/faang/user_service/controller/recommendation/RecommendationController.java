package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.SkillOfferMapper;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final RecommendationMapper recommendationMapper;
    private final SkillOfferMapper skillOfferMapper;

    @PostMapping
    public ResponseEntity<RecommendationDto> giveRecommendation(
            @RequestBody RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        recommendation.setSkillOffers(
                skillOfferMapper.toSkillOfferList(recommendationDto.getSkillOffers())
        );

        recommendation = recommendationService.create(recommendation);

        RecommendationDto responseDto = recommendationMapper.toDto(recommendation);
        responseDto.setSkillOffers(
                skillOfferMapper.toSkillOfferDtoList(recommendation.getSkillOffers())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PutMapping
    public ResponseEntity<RecommendationDto> updateRecommendation(
            @RequestBody RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        recommendation.setSkillOffers(
                skillOfferMapper.toSkillOfferList(recommendationDto.getSkillOffers())
        );

        recommendation = recommendationService.update(recommendation);

        RecommendationDto responseDto = recommendationMapper.toDto(recommendation);
        responseDto.setSkillOffers(
                skillOfferMapper.toSkillOfferDtoList(recommendation.getSkillOffers())
        );
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
        List<RecommendationDto> recommendations = recommendationMapper.toRecommendationDtoList(
                recommendationService.getAllUserRecommendations(receiverId));
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<RecommendationDto>> getAllGivenRecommendations(
            @PathVariable long authorId) {
        List<RecommendationDto> recommendations = recommendationMapper.toRecommendationDtoList(
                recommendationService.getAllGivenRecommendations(authorId));
        return ResponseEntity.ok(recommendations);
    }
}
