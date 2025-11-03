package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.controller.facade.recommendation.request.RecommendationRequestFacade;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("recommendations")
public class RecommendationController {

    private final RecommendationRequestFacade recommendationRequestFacade;

    @PostMapping
    public ResponseEntity<RecommendationRequestDto> create(
            @RequestBody @Valid CreateRecommendationRequestDto createRecommendationRequestDto,
            BindingResult bindingResult) {
        return new ResponseEntity<>(recommendationRequestFacade.create(
                createRecommendationRequestDto, bindingResult), HttpStatus.OK);
    }

    @PostMapping("getByFiler")
    public ResponseEntity<List<RecommendationRequestDto>> getByFilter(
            @RequestBody RecommendationRequestFilterDto recommendationRequestFilterDto) {
        return new ResponseEntity<>(recommendationRequestFacade.getByFilter(
                recommendationRequestFilterDto), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<RecommendationRequestDto> getById(@PathVariable long id) {
        return new ResponseEntity<>(recommendationRequestFacade.getById(id), HttpStatus.OK);
    }

    @PatchMapping("accept/{id}")
    public ResponseEntity<HttpStatus> accept(@PathVariable long id) {
        recommendationRequestFacade.accept(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PatchMapping("reject/{id}")
    public ResponseEntity<?> reject(@PathVariable long id,
                                    @RequestBody @Valid RejectionDto rejectionDto,
                                    BindingResult bindingResult) {
        recommendationRequestFacade.reject(id, rejectionDto, bindingResult);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
