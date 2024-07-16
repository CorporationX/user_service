package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Qualifier;
import school.faang.user_service.controller.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import java.util.List;
@Qualifier

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface RecommendationRequestMapper {
    @Mapping(source = "requesterId", target = "requester.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    RecommendationRequest ToEntity(RecommendationRequestDto requestDto);

    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "skills", target = "skillsIds", qualifiedByName = "skillsID")
    RecommendationRequestDto toDto(RecommendationRequest request);

    @Named("skillsID")
    default List<Long> getSkillsIds(List<SkillRequest> skills) {
        return skills.stream().map(SkillRequest::getId).toList();
    }
}