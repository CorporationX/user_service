package school.faang.user_service.controller.event;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@Controller
@AllArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;


    @PostMapping("/recommendation")
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendationDto) {
        return recommendationService.giveRecommendation(recommendationDto);
    }

    @PutMapping("/recommendation/{id}")
    public RecommendationDto updateRecommendation(@RequestBody RecommendationDto updated, @PathVariable Long id) {
        validateId(id);
        validateData(updated);
        return recommendationService.updateRecommendation(updated, id);
    }

    @DeleteMapping("/recommendation/{id}")
    public void deleteRecommendation(@PathVariable Long id) {
        validateId(id);
        recommendationService.deleteRecommendation(id);
    }

    @GetMapping("/recommendation/receiver/{receiverId}")
    public List<RecommendationDto> getAllUserRecommendations(@PathVariable Long receiverId) {
        validateId(receiverId);
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    @GetMapping("/recommendation/given/{authorId}")
    public List<RecommendationDto> getAllUserGivenRecommendations(@PathVariable Long authorId) {
        validateId(authorId);
        return recommendationService.getAllUserGivenRecommendations(authorId);
    }

    private void validateData(RecommendationDto recommendationDto) {
        if(recommendationDto == null) {
            throw new IllegalArgumentException("RecommendationDto is null");
        }
        if(recommendationDto.getAuthorId() == null) {
            throw new IllegalArgumentException("AuthorId is null");
        }
        if(recommendationDto.getReceiverId() == null) {
            throw new IllegalArgumentException("ReceiverId is null");
        }
        if(recommendationDto.getContent() == null) {
            throw new IllegalArgumentException("Content is null");
        }
    }

    private void validateId(Long id) {
        if(id > 1) {
            throw new IllegalArgumentException("Id is null");
        }
    }
}

