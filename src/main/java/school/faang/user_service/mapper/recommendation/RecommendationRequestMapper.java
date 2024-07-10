package school.faang.user_service.mapper.recommendation;

import org.mapstruct.*;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "skillIds", source = "skills", qualifiedByName = "mapSkills")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    @Named("mapSkills")
    default List<Long> mapSkills(List<SkillRequest> skills) {
        return skills.stream().map(SkillRequest::getId).collect(Collectors.toList());
    }

    @Mapping(target = "requester", source = "requesterId", qualifiedByName = "mapRequesterId")
    @Mapping(target = "receiver", source = "receiverId", qualifiedByName = "mapReceiverId")
    @Mapping(target = "skills", source = "skillIds", qualifiedByName = "mapSkillIds")
    RecommendationRequest toEntity(RecommendationRequestDto recommendationRequestDto);


    @Named("mapRequesterId")
    default User mapRequesterId(Long requesterId) {
        if (requesterId == null) {
            return null;
        }
        User requester = new User();
        requester.setId(requesterId);
        return requester;
    }

    @Named("mapReceiverId")
    default User mapReceiverId(Long receiverId) {
        if (receiverId == null) {
            return null;
        }
        User receiver = new User();
        receiver.setId(receiverId);
        return receiver;
    }

    @Named("mapSkillIds")
    default List<SkillRequest> mapSkillIds(List<Long> skillIds) {
        return skillIds.stream().map(id -> {
            SkillRequest skillRequest = new SkillRequest();
            skillRequest.setId(id);
            return skillRequest;
        }).collect(Collectors.toList());
    }
}
