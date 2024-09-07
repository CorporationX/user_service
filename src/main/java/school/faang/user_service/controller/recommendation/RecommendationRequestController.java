package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recomendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recomendation.CreateRecommendationResponseDto;
import school.faang.user_service.dto.recomendation.FilterRecommendationRequestsDto;
import school.faang.user_service.dto.recomendation.RecommendationRequestDto;
import school.faang.user_service.dto.recomendation.RejectRecommendationRequestDto;
import school.faang.user_service.service.recomendation.RecommendationRequestMapper;
import school.faang.user_service.service.recomendation.RecommendationRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("recommendation/request")
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;
    private final RecommendationRequestMapper mapper;

    @PostMapping
    public CreateRecommendationResponseDto createRequestRecommendation(
        @Valid @RequestBody
        CreateRecommendationRequestDto createRecommendationRequest
    ) {
        Long recommendationRequestId = this.recommendationRequestService.create(
            this.mapper.toEntity(createRecommendationRequest),
            createRecommendationRequest.getSkills()
        );

        return new CreateRecommendationResponseDto(recommendationRequestId);
    }

    @GetMapping("search")
    public List<RecommendationRequestDto> findRequestRecommendations(
        @ModelAttribute
        FilterRecommendationRequestsDto filterRecommendationRequestsDto
    ) {
        var result = this.recommendationRequestService.getRecommendationRequests(filterRecommendationRequestsDto);

        return result.stream().map(this.mapper::toDto).toList();
    }

    @GetMapping("{id}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable Long id) {
        return this.mapper.toDto(
            this.recommendationRequestService.findRequestById(id)
        );
    }

    @PutMapping("{id}/reject")
    public RecommendationRequestDto rejectRequest(
        @PathVariable
        Long id,
        @Valid
        @RequestBody
        RejectRecommendationRequestDto rejectionDto
    ) {
        rejectionDto.setId(id);
        return this.mapper.toDto(
            this.recommendationRequestService.rejectRequest(rejectionDto)
        );
    }
}
