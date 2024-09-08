package school.faang.user_service.mapper.recomendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recomendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {

    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "skills", target = "skillsIds", qualifiedByName = "mapSkillsReqsToSkillsReqsIds")
    RecommendationRequestDto mapToDto(RecommendationRequest recommendationRequest);

    List<RecommendationRequestDto> mapToDto(List<RecommendationRequest> recommendationRequests);

    @Mapping(source = "requesterId", target = "requester.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    @Mapping(target = "skills", ignore = true)
    RecommendationRequest mapToEntity(RecommendationRequestDto recommendationRequestDto);



    @Named("mapSkillsReqsToSkillsReqsIds")
    static List<Long> mapSkillsReqsToSkillsReqsIds(List<SkillRequest> skillRequests) {
        return skillRequests.stream()
                .map(SkillRequest::getId)
                .toList();
    }
}
