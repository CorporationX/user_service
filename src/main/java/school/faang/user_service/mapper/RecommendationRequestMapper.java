package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = MapperMethods.class)
public interface RecommendationRequestMapper {
    @Mapping(source = "skills", target = "skillsIds", qualifiedByName = "skillsToSkillsIds")
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    @Mapping(source = "skillsIds", target = "skills",
            qualifiedByName = "getSkillRequests")
    @Mapping(source = "requesterId", target = "requester.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    RecommendationRequest toEntity(RecommendationRequestDto recommendationRequestDto);

    @Named("skillsToSkillsIds")
    default List<Long> mapperSkillsToSkillsIds(List<SkillRequest> skills) {
        return skills.stream().map(SkillRequest::getId).toList();
    }
}
