package school.faang.user_service.mapper.recommendation;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {

    RecommendationRequest toEntity(RecommendationRequestDto dto);

    @Mapping(target = "requestId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "skillIds", qualifiedByName = "listToIds", source = "skills")
    RecommendationRequestDto toDto(RecommendationRequest entity);

    @Named("listToIds")
    default List<Long> listToIds(List<SkillRequest> list) {
        return list.stream().map(SkillRequest::getId).toList();
    }
}
