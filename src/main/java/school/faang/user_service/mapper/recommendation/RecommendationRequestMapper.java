package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {
    @Mapping(source = "skills", target = "skillIds", qualifiedByName = "map")
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    @Mapping(target = "skills", ignore = true)
    RecommendationRequest toEntity(RecommendationRequestDto recommendationRequestDto);

    @Mapping(source = "rejectionReason", target = "reason")
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    RejectionDto toRejectionDto(RecommendationRequest rejectionRequest);

    @Mapping(source = "reason", target = "rejectionReason")
    @Mapping(target = "skills", ignore = true)
    RecommendationRequest toEntity(RejectionDto rejectionDto);

    @Named("map")
    default List<Long> map(List<SkillRequest> skills) {
        if (skills != null) {
            return skills.stream().map(SkillRequest::getId).toList();
        }
        return new ArrayList<>();
    }
}
