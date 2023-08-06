package school.faang.user_service.mapper.recommendation;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {

    @Mapping(target = "requester.id", source = "requestId", ignore = true)
    @Mapping(target = "receiver.id", source = "receiverId", ignore = true)
    @Mapping(target = "skills", source = "skillIds", ignore = true)
    RecommendationRequest toEntity(RecommendationRequestDto dto);

//    @Mapping(target = "requestId", source = "requester.id")
//    @Mapping(target = "receiverId", source = "receiver.id")
//    @Mapping(target = "skillIds", source = "skills", qualifiedByName = "listToIds")
//    RecommendationRequestDto toDto(RecommendationRequest entity);

    default List<Long> listToIds(List<RecommendationRequest> list) {
        return list.stream().map(RecommendationRequest::getId).toList();
    }
}
