package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.controller.recommendation.RejectionDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface RecommendationRequestRejectionMapper {
    @Mapping(source = "reason", target = "rejectionReason")
    RecommendationRequest ToEntity(RejectionDto requestDto);

    @Mapping(source = "rejectionReason", target = "reason")
    RejectionDto toDto(RecommendationRequest request);
}