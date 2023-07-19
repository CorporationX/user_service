package school.faang.user_service.mappers;

import org.mapstruct.*;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillOfferMapper {

    @Mapping(target = "skill.id", source = "skill")
    @Mapping(target = "recommendation.id", source = "recommendation")
    SkillOffer toEntity(SkillOfferDto dto);

    @Mapping(source ="skill.id", target = "skill" )
    @Mapping(source ="recommendation.id", target = "recommendation")
    SkillOfferDto toDto(SkillOffer entity);

    @Named("mapSkillOffersDtoToEntity")
    default List<SkillOfferDto> toSkillOfferDtos(List<SkillOffer> skills) {
        return skills.stream()
                .map(this::toDto)
                .toList();
    }
}
