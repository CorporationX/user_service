package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recomendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface SkillOfferMapper {
    @Mapping(source = "skill.id", target = "skillId")
    SkillOfferDto toDto(SkillOffer entity);

    @Mapping(source = "skillId", target = "skill.id")
    SkillOffer toEntity(SkillOfferDto dto);

    List<SkillOfferDto> toDtoList(List<SkillOffer> entities);
}
