package school.faang.user_service.controller.recommendation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/recommendation")

@Tag(name = "Рекомендации", description = "Взаимодействие с рекомендациями")
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @Operation(
            summary = "Создать рекомендацию",
            description = "Позволяет создать рекомендацию"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecommendationRequestDto create(@RequestBody @Valid CreateRecommendationRequestDto recommendationDto) {
        return recommendationRequestService.create(recommendationDto);
    }

    @Operation(
            summary = "Список Рекомендаций",
            description = "Позволяет получить список рекомендаций"
    )
    @GetMapping
    public List<RecommendationRequestDto> getByFilters(@Valid RecommendationRequestFilterDto filters) {
        return recommendationRequestService.getByFilters(filters);
    }

    @Operation(
            summary = "Показать рекомендацию",
            description = "Позволяет увидеть текущую информацию о рекомендации"
    )
    @GetMapping("{id}")
    public RecommendationRequestDto getById(@PathVariable long id) {
        return recommendationRequestService.getById(id);
    }

    @Operation(
            summary = "Принять рекомендацию",
            description = "Позволяет принять рекомендацию"
    )
    @PatchMapping("{id}/accept")
    public void accept(long id) {
        recommendationRequestService.accept(id);
    }

    @Operation(
            summary = "Отменить рекомендацию",
            description = "Позволяет отменить рекомендацию"
    )
    @PatchMapping("{id}/reject")
    public void reject(long id, @Valid RejectionDto rejection) {
        recommendationRequestService.reject(id, rejection);
    }
}
