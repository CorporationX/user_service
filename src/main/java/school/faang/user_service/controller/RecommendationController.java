package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.validator.RecommendationValidator;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendation")
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final RecommendationValidator recommendationValidator;
    private final UserContext userContext;

    @PostMapping
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendationDto) {
        long authorId = userContext.getUserId();
        recommendationDto.setAuthorId(authorId);

        recommendationValidator.validateRecommendation(recommendationDto);
        return recommendationService.create(recommendationDto);
    }

    @PutMapping
    public RecommendationDto updateRecommendation(@RequestBody RecommendationDto recommendationDto) {
        long userId = userContext.getUserId();
        long authorId = recommendationDto.getAuthorId();

        recommendationValidator.validateUserAndAuthorIds(userId, authorId);

        recommendationValidator.validateRecommendation(recommendationDto);
        return recommendationService.update(recommendationDto);
    }

    @DeleteMapping
    public void deleteRecommendation(@RequestBody RecommendationDto recommendationDto) {
        recommendationValidator.validateRecommendation(recommendationDto);
        recommendationService.delete(recommendationDto.getId());
    }

    @GetMapping("/user/{receiver_id}")
    public Page<RecommendationDto> getAllUserRecommendations(@PathVariable("receiver_id") long receiverId,
                                                             @RequestParam(name = "page_number") int pageNum,
                                                             @RequestParam(name = "page_size") int pageSize) {
        recommendationValidator.validateId(receiverId);
        return recommendationService.getAllUserRecommendation(receiverId, pageNum, pageSize);
    }

    @GetMapping("/all/{author_id}")
    public Page<RecommendationDto> getAllRecommendation(@PathVariable("author_id") long authorId,
                                                        @RequestParam(name = "page_number") int pageNum,
                                                        @RequestParam(name = "page_size") int pageSize) {
        recommendationValidator.validateId(authorId);
        return recommendationService.getAllRecommendation(authorId, pageNum, pageSize);
    }
}