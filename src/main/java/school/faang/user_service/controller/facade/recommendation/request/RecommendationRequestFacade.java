package school.faang.user_service.controller.facade.recommendation.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.service.recommendation.RecommendationRequestService;
import school.faang.user_service.service.recommendation.validator.RecommendationRequestValidator;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationRequestFacade {

    private final RecommendationRequestMapper recommendationRequestMapper;
    private final RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto getById(long id) {
        RecommendationRequest foundRecommendationRequest = recommendationRequestService.getById(id);
        return recommendationRequestMapper.toRecommendationRequestDto(foundRecommendationRequest);
    }

    public List<RecommendationRequestDto> getByFilter(RecommendationRequestFilterDto recommendationRequestFilterDto) {
        List<RecommendationRequest> recommendationRequests =
                recommendationRequestService.getByFilters(recommendationRequestFilterDto);
        return recommendationRequestMapper.toRecommendationRequestListDto(recommendationRequests);
    }

    public RecommendationRequestDto create(CreateRecommendationRequestDto createRecommendationRequestDto,
                                           BindingResult bindingResult) {
        RecommendationRequestValidator.handleValidationError(bindingResult);
        RecommendationRequest recommendationRequestForCreation = recommendationRequestMapper
                .toRecommendationRequest(createRecommendationRequestDto);
        RecommendationRequest createdRequest = recommendationRequestService.create(
                recommendationRequestForCreation,
                createRecommendationRequestDto.getSkillIds(), createRecommendationRequestDto.getReceiverId()
        );
        return recommendationRequestMapper.toRecommendationRequestDto(createdRequest);
    }

    public void reject(long id, RejectionDto rejectionDto, BindingResult bindingResult) {
        RecommendationRequestValidator.handleValidationError(bindingResult);
        recommendationRequestService.reject(id, rejectionDto);
    }

    public void accept(long id) {
        recommendationRequestService.accept(id);
    }
}
