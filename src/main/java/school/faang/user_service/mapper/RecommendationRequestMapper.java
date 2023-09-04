package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.dto.redis.EventRecommendationRequestDto;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", typeConversionPolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {

    @Mapping(source = "skills", target = "skillsId", qualifiedByName = "mapToId")
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "recommendation.id", target = "recommendationId")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    @Mapping(source = "requesterId", target = "requester.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    @Mapping(source = "recommendationId", target = "recommendation.id")
    RecommendationRequest toEntity(RecommendationRequestDto recommendationRequestDto);

    @Mapping(source = "requester.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "id", target = "requestId")
    EventRecommendationRequestDto toEventDto(RecommendationRequest recommendationRequest);

    @Named("mapToId")
    default List<Long> mapToId(List<SkillRequest> skills) {
        if (skills == null) {
            return Collections.emptyList();
        }
        return skills.stream()
                .map(skillRequest -> skillRequest.getSkill().getId())
                .toList();
    }
}
