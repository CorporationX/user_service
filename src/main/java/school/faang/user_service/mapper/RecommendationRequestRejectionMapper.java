package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Qualifier;
import school.faang.user_service.controller.recommendation.RejectionDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Qualifier

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface RecommendationRequestRejectionMapper {
    @Mapping(source = "reason", target = "rejectionReason")
    RecommendationRequest ToEntity(RejectionDto requestDto);

    @Mapping(source = "rejectionReason", target = "reason")
    RejectionDto toDto(RecommendationRequest request);
}