package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.request.RecommendationRequestDto;
import school.faang.user_service.dto.response.RecommendationRequestResponseDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RecommendationRequestMapper {

    @Mappings({
            @Mapping(target = "requester.id", source = "requesterId"),
            @Mapping(target = "receiver.id", source = "receiverId")
    })
    RecommendationRequest toEntity(RecommendationRequestDto requestDto);

    @Mappings({
            @Mapping(target = "requester", source = "requester.username"),
            @Mapping(target = "receiver", source = "receiver.username"),
            @Mapping(target = "status", source = "status", qualifiedByName = "mapStatus"),
            @Mapping(target = "skills", source = "skills", qualifiedByName = "mapSkills")
    })
    RecommendationRequestResponseDto toResponse(RecommendationRequest recommendationRequest);

    List<RecommendationRequestResponseDto> toResponse(List<RecommendationRequest> recommendationRequests);

    @Named("mapStatus")
    default String mapStatus(Enum<?> status) {
        return status != null ? status.name() : null;
    }

    @Named("mapSkills")
    default List<String> mapSkills(List<SkillRequest> skillRequests) {
        if (skillRequests == null) {
            return null;
        }
        return skillRequests.stream()
                .map(skillRequest -> skillRequest.getSkill().getTitle())
                .toList();
    }

}
