package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.model.dto.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping()
    public RecommendationDto giveRecommendation(@RequestBody @Valid RecommendationDto recommendationDto) {
        return recommendationService.create(recommendationDto);
    }

    @PutMapping("/{id}")
    public RecommendationDto updateRecommendation(@PathVariable long id, @RequestBody RecommendationDto recommendationDto) {
        return recommendationService.update(id, recommendationDto);
    }

    @DeleteMapping("/{id}")
    public void deleteRecommendation(@PathVariable long id) {
        recommendationService.delete(id);
    }

    @GetMapping("/user/{receiverId}")
    public List<RecommendationDto> getAllUserRecommendations(@PathVariable long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }
}
