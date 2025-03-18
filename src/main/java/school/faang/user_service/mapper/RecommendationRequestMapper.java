package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface RecommendationRequestMapper {

    @Mapping(target = "id", source = "dto.id")
    @Mapping(target = "requester", source = "requester")
    @Mapping(target = "receiver", source = "receiver")
    @Mapping(target = "status", source = "dto.status")
    @Mapping(target = "rejectionReason", source = "dto.rejectionReason")
    @Mapping(target = "recommendation", source = "dto.recommendation")
    @Mapping(target = "createdAt", source = "dto.createdAt")
    @Mapping(target = "updatedAt", source = "dto.updatedAt")
    @Mapping(target = "skills", ignore = true)
    RecommendationRequest toEntity(RecommendationRequestDto dto, User requester, User receiver);

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "requesterId", source = "entity.requester.id")
    @Mapping(target = "receiverId", source = "entity.receiver.id")
    @Mapping(target = "status", source = "entity.status")
    @Mapping(target = "rejectionReason", source = "entity.rejectionReason")
    @Mapping(target = "recommendation", source = "entity.recommendation")
    @Mapping(target = "createdAt", source = "entity.createdAt")
    @Mapping(target = "updatedAt", source = "entity.updatedAt")
    @Mapping(target = "skillIds", ignore = true)
    RecommendationRequestDto toDto(RecommendationRequest entity);
}
