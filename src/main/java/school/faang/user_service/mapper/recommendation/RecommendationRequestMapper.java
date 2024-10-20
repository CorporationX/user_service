package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.model.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.model.entity.recommendation.RecommendationRequest;
import school.faang.user_service.model.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {

    @Mapping(source = "skills", target = "skillsId", qualifiedByName = "mapSkillRequests")
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    @Mapping(target = "skills", ignore = true)
    @Mapping(source = "requesterId", target = "requester.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    RecommendationRequest toEntity(RecommendationRequestDto requestDto);

    @Named("mapSkillRequests")
    default List<Long> mapSkillRequests(List<SkillRequest> skillRequests){
        return skillRequests
                .stream()
                .map(SkillRequest::getId)
                .toList();
    }

    List<RecommendationRequestDto> toDto(List<RecommendationRequest> recommendationRequests);
}
