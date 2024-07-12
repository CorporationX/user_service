package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = SkillRequestMapper.class
)
public interface RecommendationRequestMapper {

    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "recieverId")
    @Mapping(source = "skills", target = "skills", qualifiedByName = "toDtoList")
    RecommendationRequestDto toDtoList(RecommendationRequest recommendationRequest);

    @Mapping(source = "requesterId", target = "requester.id")
    @Mapping(source = "recieverId", target = "receiver.id")
    @Mapping(source = "skills", target = "skills", qualifiedByName = "toEntityList")
    RecommendationRequest toEntityList(RecommendationRequestDto recommendationRequestDto);

    @Named("toDtoList")
    default List<SkillRequestDto> toDtoList(List<SkillRequest> skillRequests) {
        return skillRequests.stream()
                .map(skillRequest -> Mappers.getMapper(SkillRequestMapper.class).toDto(skillRequest))
                .toList();
    }

    @Named("toEntityList")
    default List<SkillRequest> toEntityList(List<SkillRequestDto> skillRequestsDto) {
        return skillRequestsDto.stream()
                .map(skillRequestDto -> Mappers.getMapper(SkillRequestMapper.class).toEntity(skillRequestDto))
                .toList();
    }
}
