package school.faang.user_service.controller;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@RestController
@AllArgsConstructor
public class RecommendationController {
    private RecommendationService recommendationService;

    @PostMapping("/recommendation")
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        return recommendationService.create(recommendation);
    }

    @PutMapping("/recommendation/{id}")
    public RecommendationDto updateRecommendation(@RequestBody RecommendationDto updated, @PathVariable long id) {
        validateId(id);
        validateData(updated);
        return recommendationService.updateRecommendation(updated, id);
    }

    @DeleteMapping("/recommendation/{id}")
    public void deleteRecommendation(@PathVariable long id) {
        validateId(id);
        recommendationService.deleteRecommendation(id);
    }

    @GetMapping("/recommendation/receiver/{receiverId}")
    public List<RecommendationDto> getAllUserRecommendations(@PathVariable long receiverId){
        validateId(receiverId);
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    @GetMapping("/recommendation/given/{authorId}")
    public List<RecommendationDto> getAllUserGivenRecommendations(@PathVariable long authorId) {
        validateId(authorId);
        return recommendationService.getAllUserGivenRecommendations(authorId);
    }

    private void validateRecommendation(RecommendationDto recommendationDto) {
        if (recommendationDto == null){
            throw new DataValidationException("RecommendationDto cannot be null");
        }
        if (recommendationDto.getContent() == null || recommendationDto.getContent().isBlank()) {
            throw new DataValidationException("Recommendation content cannot be empty");
        }
    }

    private void validateId(Long id) {
        if (id < 1)
            throw new DataValidationException("Id is null");
    }

    private void validateData(RecommendationDto recommendationDto) {
        if (recommendationDto == null) {
            throw new DataValidationException("RecommendationDto is null");
        }
        if (recommendationDto.getAuthorId() == null) {
            throw new DataValidationException("AuthorId is null");
        }
        if (recommendationDto.getReceiverId() == null) {
            throw new DataValidationException("ReceiverId is null");
        }
        if (recommendationDto.getContent() == null) {
            throw new DataValidationException("Content is null");
        }
    }
}
