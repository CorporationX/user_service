package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Qualifier;
import school.faang.user_service.controller.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Qualifier

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface RecommendationRequestFilterMapper {
    @Mapping(source = "requesterName", target = "requester.username")
    @Mapping(source = "receiverName", target = "receiver.username")
    @Mapping(source = "recommendationId", target = "recommendation.id")
    RecommendationRequest ToEntity(RequestFilterDto requestDto);

    @Mapping(source = "requester.username", target = "requesterName")
    @Mapping(source = "receiver.username", target = "receiverName")
    @Mapping(source = "recommendation.id", target = "recommendationId")
    RequestFilterDto toDto(RecommendationRequest request);
}