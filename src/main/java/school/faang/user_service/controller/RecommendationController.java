package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    //FIXME: по задаче этот метод обрабатывает запрос и предоставляет кол-во подписчиков (что за кол-во подписчиков?)
//FIXME: предложения скилла не является обязательным (это странно, т.к метод нужен, чтобы добавить в список
// скиллов подтвержденный скилл)
    @PostMapping()
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendationDto) {
        checkIfContentEmpty(recommendationDto);
        return recommendationService.create(recommendationDto);
    }

    @PutMapping("/{id}")
    public RecommendationDto updateRecommendation(@PathVariable long id, @RequestBody RecommendationDto recommendationDto) {
        checkIfContentEmpty(recommendationDto);
        if (recommendationDto.getId() != null && recommendationDto.getId() != id) {
            throw new DataValidationException("Mismatched id in the URL and the body");
        }
        recommendationDto.setId(id);
        return recommendationService.update(recommendationDto);
    }

    @DeleteMapping("/{id}")
    public void deleteRecommendation(@PathVariable long id) {
        recommendationService.delete(id);
    }

    @GetMapping("/user/{receiverId}")
    public List<RecommendationDto> getAllUserRecommendations(@PathVariable long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    private void checkIfContentEmpty(RecommendationDto recommendationDto) {
        if (recommendationDto.getContent() == null || recommendationDto.getContent().isBlank()) {
            throw new DataValidationException("Recommendation should contain non-empty content");
        }
    }
}
