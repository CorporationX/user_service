package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recomendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recomendation.FilterRecommendationRequestsDto;
import school.faang.user_service.dto.recomendation.RecommendationRequestDto;
import school.faang.user_service.dto.recomendation.RejectRecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.service.recomendation.RecommendationRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendation/request")
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;
    private final RecommendationRequestMapper mapper;

    @PostMapping
    public RecommendationRequestDto create(@Valid @RequestBody CreateRecommendationRequestDto dto) {
        RecommendationRequest recommendationRequest = mapper.toEntity(dto);
        List<Long> skills = dto.getSkills();

        RecommendationRequest result = recommendationRequestService.create(
                recommendationRequest,
                skills
        );

        return mapper.toDto(result);
    }

    @GetMapping("/search")
    public List<RecommendationRequestDto> findRequestRecommendations(
            FilterRecommendationRequestsDto filterDto
    ) {
        List<RecommendationRequest> result = recommendationRequestService.getRecommendationRequests(filterDto);

        return mapper.listEntitiesToListDto(result);
    }

    @GetMapping("/{id}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable Long id) {
        RecommendationRequest recommendationRequest = recommendationRequestService.findRequestById(id);

        return mapper.toDto(recommendationRequest);
    }

    @PutMapping("{id}/reject")
    public RecommendationRequestDto rejectRequest(
            @PathVariable Long id,
            @Valid @RequestBody RejectRecommendationRequestDto rejectionDto
    ) {
        rejectionDto.setId(id);
        RecommendationRequest recommendationRequest = mapper.toRejectEntity(rejectionDto);
        RecommendationRequest result = recommendationRequestService.rejectRequest(recommendationRequest);

        return mapper.toDto(result);
    }
}
