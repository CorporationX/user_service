package school.faang.user_service.mapper.recommendation;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.skill.SkillRequestMapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = SkillRequestMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RecommendationRequestMapper {
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "skills", target = "skillIds", qualifiedByName = "mapToIds")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    @Mapping(target = "receiver.id", ignore = true)
    @Mapping(target = "requester.id", ignore = true)
    @Mapping(target = "skills", ignore = true)
    RecommendationRequest toEntity(RecommendationRequestDto recommendationRequestDto);

    @Named("mapToIds")
    default List<Long> mapToIds(List<SkillRequest> skills) {
        return skills.stream()
                .map(SkillRequest::getId)
                .collect(Collectors.toList());
    }
}
