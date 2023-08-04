package school.faang.user_service.mapper;

import org.mapstruct.*;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.FIELD)
public interface SkillOfferMapper {

    @Mapping(source = "skill", target = "skill.id")
    @Mapping(source = "recommendation", target = "recommendation.id")
    SkillOffer toEntity(SkillOfferDto dto);

    @Mapping(source = "skill.id", target = "skill")
    @Mapping(source = "recommendation.id", target = "recommendation")
    SkillOfferDto toDto(SkillOffer entity);

    @Named("toSkillOfferDtos")
    default List<SkillOfferDto> toSkillOfferDtos(List<SkillOffer> skills) {
        if (skills == null) {
            return Collections.emptyList();
        }
        return skills.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
