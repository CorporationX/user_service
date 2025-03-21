package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/recommendation")
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping()
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendationDto) {
        log.info("The process of transmitting the recommendation to the user begins");
        RecommendationDto returnedRecommendation = recommendationService.create(recommendationDto);
        log.info("The recommendation has been passed");
        return returnedRecommendation;
    }

    @PutMapping()
    public RecommendationDto updateRecommendation(@RequestBody RecommendationDto recommendationDto) {
        log.info("Starting update of recommendation");
        RecommendationDto updatedRecommendationDto = recommendationService.update(recommendationDto);
        log.info("The update is finished");
        return updatedRecommendationDto;
    }

    @DeleteMapping("/{id}")
    public void deleteRecommendation(@PathVariable Long id) {
        log.info("Starting to delete the recommendation");
        recommendationService.delete(id);
        log.info("The recommendation has been removed");
    }

    @GetMapping("/{authorId}")
    public List<RecommendationDto> getAllGivenRecommendation(@PathVariable Long authorId) {
        outputOfRecommendations(recommendationService.getAllGivenRecommendation(authorId));
        return recommendationService.getAllGivenRecommendation(authorId);
    }

    @GetMapping("/{receiverId}")
    public List<RecommendationDto> getAllUserRecommendations(@PathVariable Long receiverId) {
        outputOfRecommendations(recommendationService.getAllUserRecommendations(receiverId));
        return recommendationService.getAllGivenRecommendation(receiverId);
    }

    private void outputOfRecommendations(List<RecommendationDto> recommendationDtos) {
        recommendationDtos.forEach(r ->
                log.info("Author's id: {}\nReceiver's id{}\nContent:{}\nSkillOffers:{}\nCreated at: {}",
                        r.getAuthorId(), r.getReceiverId(), r.getContent(), r.getSkillOffersDto(), r.getCreatedAt())
        );
    }
}
