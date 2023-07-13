package school.faang.user_service.service;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Component
@Mapper
public interface RecommendationRequestMapper {
    RecommendationRequest toEntity(RecommendationRequestDto dto);
}
