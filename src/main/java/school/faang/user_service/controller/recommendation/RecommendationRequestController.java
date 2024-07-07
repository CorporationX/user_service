package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService service;
    private final RecommendationRequestMapper mapper;

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest == null) {
            throw new NullPointerException("Empty request");
        }
        RecommendationRequest entity = mapper.toEntry(recommendationRequest);
        service.create(entity);
        return mapper.toDto(entity);
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RequestFilterDto filter) {
        if (filter == null) {
            throw new NullPointerException("Empty filter");
        }
        return service.getRequests(filter).stream()
                .map(mapper::toDto)
                .toList();
    }

    public RecommendationRequestDto getRecommendationRequest(long id) {
        return mapper.toDto(service.getRequest(id));
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        if (rejection == null || rejection.getReason().isBlank() || rejection.getReason().isEmpty()) {
            throw new NullPointerException("Empty rejection");
        }
        return mapper.toDto(service.rejectRequest(id, rejection));
    }
}
