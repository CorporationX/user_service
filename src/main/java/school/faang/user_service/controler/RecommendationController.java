package school.faang.user_service.controler;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendation")
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping("/recommendation/created")
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendationDto) {
        recommendationValidation(recommendationDto);
        return recommendationService.create(recommendationDto);
    }

    @PostMapping("/recommendation/updated")
    public RecommendationDto updateRecommendation(@RequestBody RecommendationDto updated) {
        recommendationValidation(updated);
        return recommendationService.update(updated);
    }

    @PostMapping("/recommendation/deleted")
    public void deleteRecommendation(@RequestBody RecommendationDto recommendationDto) {
        recommendationValidation(recommendationDto);
        recommendationService.delete(recommendationDto.getId());
    }

    @GetMapping
    public Page<RecommendationDto> getAllUserRecommendations(@PathVariable("receiver_id") long receiverId,
                                                             @RequestParam(name = "page_number") int pageNum,
                                                             @RequestParam(name = "page_size") int pageSize) {
        idValidation(receiverId);
        return recommendationService.getAllUserRecommendation(receiverId, pageNum, pageSize);
    }

    @GetMapping
    public Page<RecommendationDto> getAllRecommendation(@PathVariable("author_id") long authorId,
                                                        @RequestParam(name = "page_number") int pageNum,
                                                        @RequestParam(name = "page_size") int pageSize) {
        idValidation(authorId);
        return recommendationService.getAllRecommendation(authorId, pageNum, pageSize);
    }

    private void recommendationValidation(RecommendationDto recommendation) {
        if (recommendation.getContent() == null || recommendation.getContent().trim().isEmpty()) {
            throw new DataValidationException("Validation failed. The text cannot be empty.");
        }
    }

    private void idValidation(long id) {
        if (id <= 0) {
            throw new DataValidationException("Id is not correct.");
        }
    }
}
