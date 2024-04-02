package school.faang.user_service.mapper;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RecommendationRequestMapper {

    public RecommendationRequestDto toDto(RecommendationRequest recommendationRequest) {
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setId(recommendationRequest.getId());
        dto.setRequesterId(recommendationRequest.getRequester().getId());
        dto.setReceiverId(recommendationRequest.getReceiver().getId());
        dto.setMessage(recommendationRequest.getMessage());
        dto.setStatus(recommendationRequest.getStatus().toString());
        dto.setCreatedAt(Date.from(recommendationRequest.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
        dto.setUpdatedAt(Date.from(recommendationRequest.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant()));
        return dto;
    }

    public List<RecommendationRequestDto> toDtoList(List<RecommendationRequest> recommendationRequests) {
        return recommendationRequests.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
