package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@RestController
@RequestMapping(value="/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @RequestMapping(value="/giveRecommendation", method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RecommendationDto> giveRecommendation(@Valid @RequestBody RecommendationDto recommendationDto) {
        return ResponseEntity.status(HttpStatus.OK).body(recommendationService.create(recommendationDto));
    }

    public RecommendationDto updateRecommendation(@Valid RecommendationDto recommendationDto) {
        return recommendationService.update(recommendationDto);
    }

    public void deleteRecommendation(long id) {
        recommendationService.delete(id);
    }

    public List<RecommendationDto> getUserAllRecommendations(long receiverId, int offset, int limit) {
        return recommendationService.getAllUserRecommendations(receiverId, offset, limit);
    }

    public List<RecommendationDto> getGivenAllRecommendations(long authorId, int offset, int limit) {
        return recommendationService.getAllGivenRecommendations(authorId, offset, limit);
    }
}