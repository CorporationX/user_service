package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {

    @Mapping(target = "message", source = "message")
    @Mapping(target = "requesterId", source = "requesterId")
    @Mapping(target = "receiverId", source = "receiverId")
    RecommendationRequestDto toDto(RecommendationRequest request);

}
