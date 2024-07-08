package school.faang.user_service.mapper;

import org.mapstruct.*;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {

    @Mappings({
            @Mapping(source = "requester.id", target = "requesterId"),
            @Mapping(source = "receiver.id", target = "receiverId"),
            @Mapping(source = "status", target = "status"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "updatedAt", target = "updatedAt"),
            @Mapping(source = "skills", target = "skills", qualifiedByName = "toSkillIds")
    })
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    @Mappings({
            @Mapping(target = "requester", ignore = true),
            @Mapping(target = "receiver", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "rejectionReason", ignore = true),
            @Mapping(target = "recommendation", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "skills", source = "skills", qualifiedByName = "toSkillRequests")
    })
    RecommendationRequest toEntity(RecommendationRequestDto recommendationRequestDto);

    @Named("toSkillIds")
    default List<Long> toSkillIds(List<SkillRequest> skillRequestList){
        return skillRequestList.stream().map(SkillRequest::getId).toList();
    }

    @Named("toSkillRequests")
    default List<SkillRequest> toSkillRequests(List<Long> skillIds){
        return skillIds.stream()
                .map(id -> {
                    SkillRequest skillRequest = new SkillRequest();
                    skillRequest.setId(id);
                    return skillRequest;
                })
                .toList();
    }
}
