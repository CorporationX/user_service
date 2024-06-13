package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.SkillAcquiredEvent;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.publisher.SkillAcquiredEventPublisher;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.validator.RecommendationValidator;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendation")
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final RecommendationValidator recommendationValidator;
    private final UserContext userContext;
    private final SkillAcquiredEventPublisher skillAcquiredEventPublisher;

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

    @GetMapping("/skill/publish/{id}")
    public void skillPublish(@PathVariable long id) {
        skillAcquiredEventPublisher.publish(new SkillAcquiredEvent(id, 2, 3L));
    }
}